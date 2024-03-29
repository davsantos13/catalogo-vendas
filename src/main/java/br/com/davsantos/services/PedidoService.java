package br.com.davsantos.services;

import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import br.com.davsantos.entities.Cliente;
import br.com.davsantos.entities.ItemPedido;
import br.com.davsantos.entities.PagamentoComBoleto;
import br.com.davsantos.entities.Pedido;
import br.com.davsantos.entities.enums.StatusPagamento;
import br.com.davsantos.repositories.ItemPedidoRepository;
import br.com.davsantos.repositories.PagamentoRepository;
import br.com.davsantos.repositories.PedidoRepository;
import br.com.davsantos.security.User;
import br.com.davsantos.services.exceptions.AuthorizationException;
import br.com.davsantos.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private BoletoService boletoService;

	@Autowired
	private PagamentoRepository pagamentoRepository;
	
	@Autowired
	private ProdutoService produtoService;
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;

	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private EmailService emailService;
	
	
	public Pedido findById(Integer id) {
		if (id == null)
			return null;
		Optional<Pedido> pedido = pedidoRepository.findById(id);
		
		return pedido.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! ID : " + id + ", TIPO : " + Pedido.class.getName()));
	}
	
	@Transactional
	public Pedido insert (Pedido pedido) {
		pedido.setId(null);
		pedido.setInstante(new Date());
		pedido.setCliente(clienteService.findById(pedido.getCliente().getId()));
		pedido.getPagamento().setStatusPagamento(StatusPagamento.PENDENTE);
		pedido.getPagamento().setPedido(pedido);
		
		if (pedido.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) pedido.getPagamento();
			boletoService.preencherPagtoBoleto(pagto, pedido.getInstante());
		}
		
		pedido = pedidoRepository.save(pedido);
		pagamentoRepository.save(pedido.getPagamento());
		
		for (ItemPedido item : pedido.getItens()) {
			item.setDesconto(0.0);
			item.setProduto(produtoService.findById(item.getProduto().getId()));
			item.setPreco(item.getProduto().getPreco());
			item.setPedido(pedido);
		}
		
		itemPedidoRepository.saveAll(pedido.getItens());
		
		emailService.sendOrderConfirmationHtmlEmail(pedido);
		return pedido;
	}
	
	public Page<Pedido> findByPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		User user = UserS.authenticate();
		if(user == null) {
			throw new AuthorizationException("Acesso Negado!");
		}
		
		PageRequest request = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		Cliente cliente = clienteService.findById(user.getId());
		
		return pedidoRepository.findByCliente(cliente, request);	
	}
	
}

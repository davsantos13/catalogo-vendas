package br.com.davsantos.entities.enums;

public enum TipoCliente {

	PESSOA_FISICA (1, "Pessoa Física"),
	PESSOA_JURIDICA(2, "Pessoa Jurídica");
	
	private int codigo;
	private String descricao;
	
	private TipoCliente(int codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	public int getCodigo() {
		return codigo;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public static TipoCliente toEnum(Integer codigo) {
		if (codigo == null)
			return null;
		
		for (TipoCliente cliente : TipoCliente.values() ) {
			if (codigo.equals(cliente.getCodigo())) {
				return cliente;
			}
		}
		throw new IllegalArgumentException();
	}
	
}

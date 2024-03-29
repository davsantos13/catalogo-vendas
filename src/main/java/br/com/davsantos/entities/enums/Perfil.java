package br.com.davsantos.entities.enums;

public enum Perfil {

	ADMIN (1, "ROLE_ADMIN"),
	CLIENTE (2, "ROLE_CLIENTE");
	
	private int codigo;
	private String descricao;
	
	private Perfil(int codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	public int getCodigo() {
		return codigo;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public static Perfil toEnum(Integer codigo) {
		if (codigo == null)
			return null;
		
		for (Perfil status : Perfil.values()) {
			if (codigo.equals(status.getCodigo())) {
				return status;
			}
		}
	 throw new IllegalArgumentException("Id inválido : " + codigo);
	}
	
	
}

package br.com.itau.pix.model

class RegisterBCBResponse(val keyType: TipoChave, val key: String, val bankAccount: Account, val owner: Cliente, val createdAt: String ) {

}

package br.com.itau.pix.model

data class DetailsKeyPix(
    val tipoChave: TipoChave,
    val valorChave: String,
    val titular: String,
    val cpf: String,
    val banco: String,
    val agencia : String,
    val numeroConta: String,
    val tipoConta : TipoConta,
    val criadaEm : String
) {
    var pixId: String? = "Não disponível";
    var clienteId: String? = "Não disponível"
}
package br.com.itau.pix

import javax.persistence.Entity


data class ClienteItauResponse(
    val tipo: String,
    val instituicao: Instituicao,
    val agencia: String,
    val numero: String,
    val titular: Titular
){

}


data class Titular(
    val id: String,
    val nome: String,
    val cpf: String
)


data class Instituicao(
    val nome: String,
    val ispb: String
)

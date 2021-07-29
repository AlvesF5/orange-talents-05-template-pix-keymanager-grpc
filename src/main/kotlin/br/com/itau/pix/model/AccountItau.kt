package br.com.itau.pix.model

import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.persistence.ManyToOne

@Embeddable
data class AccountItau(
    @Column(name = "conta_tipo")
    val tipo: String,
    @Column(name = "conta_instituicao")
    val nomeInstituicao: String,
    @Column(name = "conta_instituicao_ispb")
    val isbp: String,
    @Column(name = "conta_agencia")
    val agencia: String,
    @Column(name = "conta_numero")
    val numero: String,
    @Column(name = "conta_id_titular")
    val idTitular: String,
    @Column(name = "conta_nome_titular")
    val nomeTitular: String,
    @Column(name = "conta_cpf_titular")
    val cpfTitular: String
) {
}
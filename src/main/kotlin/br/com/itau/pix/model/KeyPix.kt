package br.com.itau.pix.model

import br.com.itau.pix.TipoDaConta
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class KeyPix(
    val clientId: String,
    val tipoChave: TipoChave,
    val valorChave: String,
    val tipoConta: TipoDaConta
) {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
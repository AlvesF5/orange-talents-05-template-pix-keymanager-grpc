package br.com.itau.pix.model

import br.com.itau.pix.TipoDaConta
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class KeyPix(
    @field:NotBlank val clientId: String,
    @field:NotBlank @Enumerated(EnumType.STRING) val tipoChave: TipoChave,
    @field:NotBlank val valorChave: String,
    @field:NotBlank @Enumerated(EnumType.STRING) val tipoConta: TipoDaConta
) {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
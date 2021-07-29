package br.com.itau.pix.model

import br.com.itau.pix.TipoDaConta
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class KeyPix(
    @field:NotBlank val clientId: String,
    @field:NotBlank @Enumerated(EnumType.STRING) val tipoChave: TipoChave,
    @field:Size(max = 70) var valorChave: String,
    @field:NotBlank @Enumerated(EnumType.STRING) val tipoConta: TipoDaConta,

) {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
    @Embedded
    var accountItau: AccountItau? = null
    val criadoEm : LocalDateTime = LocalDateTime.now()
}
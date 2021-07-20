package br.com.itau.pix.model


import br.com.itau.pix.TipoDaConta
import br.com.itau.pix.ValidKeyPix
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.ConstraintViolationException
import javax.validation.Validator
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


@Introspected
@ValidKeyPix
data class KeyPixRequest(
    @field:NotBlank val clientId: String,
    @field:NotBlank val tipoChave: TipoChave?,
    @field:Size(max=77) val valorChave: String,
    @field:NotBlank val tipoConta: TipoDaConta
) {

    fun transformaParaKey(validator: Validator) : KeyPix {
        val keyPix = KeyPix(
            clientId = this.clientId,
            tipoChave = this.tipoChave!!,
            valorChave = if (this.tipoChave == TipoChave.ALEATORIA) UUID.randomUUID().toString() else  this.valorChave,
            tipoConta = this.tipoConta)

        val errors = validator.validate(keyPix)

        if (!errors.isEmpty()){
            throw ConstraintViolationException(errors)
        }

        return keyPix
    }

}
package br.com.itau.pix.model

import com.sun.istack.NotNull
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class RegisterBCBRequest(@field:NotNull val keyType: TipoChave, @field:NotBlank val key: String, @field:NotNull val bankAccount: Account, @field:NotNull val owner: Cliente) {



}

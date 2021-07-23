package br.com.itau.pix.model

import io.micronaut.core.annotation.Introspected
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
data class Account(
    @field:NotBlank val participant: String,
    @field:NotBlank @field:Size(min = 4, max = 4) val branch: String,
    @field:NotBlank @field:Size(min = 6,max = 6) val accountNumber: String,
    @field:NotBlank @Enumerated(EnumType.STRING) val accountType: TipoContaBCB
) {



}

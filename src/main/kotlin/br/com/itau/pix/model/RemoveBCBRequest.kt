package br.com.itau.pix.model

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class RemoveBCBRequest(@field:NotBlank val key: String, @field:NotBlank val participant: String) {

}

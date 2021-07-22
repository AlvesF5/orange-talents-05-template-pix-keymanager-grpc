package br.com.itau.pix.model

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class PixRemoveRequest(@field:NotBlank val pixId : String, @field:NotBlank  val clienteId : String)

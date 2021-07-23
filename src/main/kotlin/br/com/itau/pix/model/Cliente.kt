package br.com.itau.pix.model

import com.sun.istack.NotNull
import io.micronaut.core.annotation.Introspected
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotBlank

@Introspected
data class Cliente(@field:NotNull @Enumerated(EnumType.STRING) val type: OwnerType, @field:NotBlank val name: String, @field:NotBlank val taxIdNumber: String ) {

}

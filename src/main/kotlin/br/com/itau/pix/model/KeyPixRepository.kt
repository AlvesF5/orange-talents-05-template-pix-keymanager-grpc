package br.com.itau.pix.model


import br.com.itau.pix.handler.ErrorAroundHandler
import io.micronaut.core.annotation.Introspected
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*
import javax.validation.constraints.NotBlank
@Introspected
@Repository
interface KeyPixRepository : JpaRepository<KeyPix, Long> {
    fun findByClientIdAndValorChave(clientId : String, valorChave : String) : Optional<KeyPix>

    fun findByIdAndClientId(@NotBlank pixId : Long, @NotBlank clientId: String) : Optional<KeyPix>

    fun findByValorChave(key : String) : Optional<KeyPix>
}
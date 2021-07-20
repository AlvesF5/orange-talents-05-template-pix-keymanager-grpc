package br.com.itau.pix.model

import br.com.itau.pix.model.KeyPix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface KeyPixRepository : JpaRepository<KeyPix, Long> {
    fun findByClientIdAndValorChave(clientId : String, valorChave : String) : Optional<KeyPix>
}
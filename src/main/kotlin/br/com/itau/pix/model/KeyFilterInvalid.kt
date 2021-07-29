package br.com.itau.pix.model

import br.com.itau.pix.clienthttp.ClienteBCB
import java.lang.IllegalArgumentException

class KeyFilterInvalid : KeyFilter {
    override fun filter(keyPixRepository: KeyPixRepository, clienteBCB: ClienteBCB) : DetailsKeyPix {
        throw IllegalArgumentException("Opção inválida!")
    }
}
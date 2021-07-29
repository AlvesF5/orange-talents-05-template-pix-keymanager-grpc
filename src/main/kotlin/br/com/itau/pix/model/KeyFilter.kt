package br.com.itau.pix.model

import br.com.itau.pix.clienthttp.ClienteBCB

interface KeyFilter {

    fun filter(keyPixRepository: KeyPixRepository,  clienteBCB: ClienteBCB) : DetailsKeyPix

}
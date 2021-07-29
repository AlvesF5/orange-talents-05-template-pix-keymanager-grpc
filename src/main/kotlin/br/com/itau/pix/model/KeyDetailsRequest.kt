package br.com.itau.pix.model

import br.com.itau.pix.InternalKeyPixDetailsRequest
import br.com.itau.pix.KeyPixDetailsRequest

class KeyDetailsRequest(val key: String, val internalQuery: InternalKeyPixDetailsRequest) {

    open fun KeyPixDetailsRequest.selectFilter() : KeyFilter{
        val escolha = when(keyPixDetailsQueryCase){
            KeyPixDetailsRequest.KeyPixDetailsQueryCase.KEY -> KeyFilterChave(key = key)
            KeyPixDetailsRequest.KeyPixDetailsQueryCase.INTERNALQUERY -> KeyFilterPixIdAndClienteId(pixId = internalQuery.pixId, clienteId = internalQuery.clienteId)
            KeyPixDetailsRequest.KeyPixDetailsQueryCase.KEYPIXDETAILSQUERY_NOT_SET -> KeyFilterInvalid()
        }

        return escolha
    }
}
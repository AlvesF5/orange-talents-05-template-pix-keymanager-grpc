package br.com.itau.pix.clienthttp

import br.com.itau.pix.model.RegisterBCBRequest
import br.com.itau.pix.model.RegisterBCBResponse
import br.com.itau.pix.model.RemoveBCBRequest
import br.com.itau.pix.model.RemoveBCBResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client


@Client("http://localhost:8082/api/v1/pix")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
interface ClienteBCB {

    @Post("/keys")
    fun keyRegisterBCB(@Body registerBCBRequest: RegisterBCBRequest) : HttpResponse<RegisterBCBResponse>

    @Delete("/keys/{key}")
    fun keyRemoveBCB(@Body removeBCBRequest : RemoveBCBRequest) : HttpResponse<RemoveBCBResponse>

}
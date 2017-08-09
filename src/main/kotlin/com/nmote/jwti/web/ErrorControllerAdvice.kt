/*
 *   Copyright 2017. Vjekoslav Nesek
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.nmote.jwti.web

import com.nmote.jwti.model.OAuthError
import com.nmote.jwti.model.OAuthException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ErrorControllerAdvice {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun oauthError(ex: OAuthException): Map<String, *> {
        return mapOf("error" to ex.error)
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun unknownError(ex: Throwable): Map<String, *> {
        log.error("Fatal error", ex)
        return mapOf("error" to OAuthError.invalid_request)
    }

    val log = LoggerFactory.getLogger(javaClass)!!
}
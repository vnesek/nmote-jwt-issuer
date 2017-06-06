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

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OAuth2Request(

        /**
         * Value 'code' for authorization code flow
         */
        var response_type: String? = null,

        /**
         * The client identifier as assigned by the authorization server, when the client was registered.
         */
        var client_id: String? = null,

        /**
         * The redirect URI registered by the client.
         */
        var redirect_uri: String? = null,

        /**
         * The possible scope of the request.
         */
        var scope: String? = null,

        /**
         * Any client state that needs to be passed on to the client request URI.
         */
        var state: String? = null,

        var grant_type: String? = null,

        var client_secret: String? = null,

        var code: String? = null
)
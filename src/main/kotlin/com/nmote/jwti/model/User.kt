package com.nmote.jwti.model

import com.fasterxml.jackson.annotation.JsonIgnore

class User : BasicSocialAccount {

    constructor() : super()

    constructor(account: SocialAccount) : super(account)

    @JsonIgnore
    var password: String = "none"

    var roles: Set<String> = emptySet()
}

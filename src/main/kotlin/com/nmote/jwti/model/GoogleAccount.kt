package com.nmote.jwti.model

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.scribejava.core.model.OAuth2AccessToken
import com.nmote.jwti.trimToNull
import org.springframework.data.annotation.Transient
import java.io.Serializable


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
class Email : Serializable {

    var type: String? = null
    var value: String? = null
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
class Image : Serializable {

    var isDefault: Boolean = false
    var url: String? = null
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
class Name : Serializable {

    var familyName: String? = null
    var givenName: String? = null
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
class Range : Serializable {

    var max: Int? = null
    var min: Int? = null
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
class GoogleAccount : SocialAccount<OAuth2AccessToken>, Serializable {

    @get:Transient
    @get:JsonIgnore
    override var accessToken: OAuth2AccessToken? = null

    @get:Transient
    @get:JsonIgnore
    override val profileName: String?
        get() {
            var result = displayName.trimToNull()
            if (result == null && name != null) {
                val b = StringBuilder(40)
                if (name.givenName != null) {
                    b.append(name.givenName)
                }
                if (name.familyName != null) {
                    if (b.length > 0) {
                        b.append(' ')
                    }
                    b.append(name.familyName)
                }
                if (b.length > 0) {
                    result = b.toString()
                }
            }
            if (result == null) {
                result = profileEmail
            }
            return result
        }

    @get:Transient
    @get:JsonIgnore
    override val accountId: String
        get() = id ?: "?"

    @get:Transient
    @get:JsonIgnore
    override val profileEmail: String?
        get() {
            val e = emails
            return if (e != null && !e.isEmpty()) e[0].value else null
        }

    @get:Transient
    @get:JsonIgnore
    override val profileImageURL: String?
        get() = image?.url

    @get:Transient
    @get:JsonIgnore
    override val socialService: String
        get() = "google"

    val ageRange: Range? = null
    val birthday: String? = null
    val circledByCount: Int = 0
    val displayName: String? = null
    val domain: String? = null
    val emails: List<Email>? = null
    val etag: String? = null
    val gender: String? = null
    val id: String? = null
    val image: Image? = null
    val isPlusUser: Boolean = false
    val kind: String? = null
    val language: String? = null
    val name: Name? = null
    val objectType: String? = null
    val url: String? = null
    val verified: Boolean = false
}

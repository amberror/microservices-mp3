package contracts.rest

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should save resource metadata"
    request{
        method 'POST'
        url '/songs'
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                name: "Bohemian Rhapsody",
                artist: "Queen",
                album: "A Night at the Opera",
                duration: "05:55",
                year: 1975
        )
    }
    response {
        body(id : 1)
        status 200
        headers {
            contentType(applicationJson())
        }
    }
}

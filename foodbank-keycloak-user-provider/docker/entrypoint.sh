#!/bin/bash
sed -i 's@###REDIRECT_URIS###@'"$REDIRECT_URIS"'@' /import/realm/*.json && \
    sed -i 's@###REDIRECT_URIS_ISIS###@'"$REDIRECT_URIS_ISIS"'@' /import/realm/*.json && \
    sed -i 's@###AUTH_SERVER_BASE_URL###@'"$AUTH_SERVER_BASE_URL"'@' /import/realm/*.json

/opt/keycloak/bin/kc.sh --verbose --optimized import --dir=/import/realm

$@


CORE_ROOT=$(cd "$(dirname "$0")/.." && pwd)

function get_var() {
  local result

  result=$(grep "$1" "$CORE_ROOT"/configuration.properties | tr -d '\r' | sed -e 's/.*=[\t ]*\([^\t #]*\).*$/\1/')
  echo $result
}


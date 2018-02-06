#!/usr/bin/env bash
set -u

DIR=$(cd "$(dirname "$0")" && pwd)

. "$DIR"/common.sh

DB=$(get_var "db.name")
USER=$(get_var "db.username")
PWD=$(get_var "db.password")
mysql -p"$PWD" -u "$USER" "$DB" "$@"

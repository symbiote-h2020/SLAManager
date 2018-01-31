#!/usr/bin/env bash
set -u

DIR=$(cd "$(dirname "$0")" && pwd)

. "$DIR"/common.sh
DROP=$("$DIR"/mysql.sh <<< "show tables" | grep -v "Tables" | sed -e's/\(.*\)/drop table \1; /')
SQL=$(echo "SET FOREIGN_KEY_CHECKS=0;" && echo $DROP)
echo "$SQL" | "$DIR"/mysql.sh

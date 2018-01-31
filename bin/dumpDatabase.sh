#!/usr/bin/env bash
if [ "$0" != "bin/dumpDatabase.sh" ]; then
	echo "Must be executed from project root"
	exit 1
fi

DIR=$(cd "$(dirname "$0")" && pwd)

. "$DIR"/common.sh

DB=$(get_var "db.name")
USER=$(get_var "db.username")
PWD=$(get_var "db.password")

mysqldump -d --password="$PWD" -u "$USER" "$DB" > sla-repository/src/main/resources/sql/atossla.sql

#!/usr/bin/env bash
set -u

if [ "$0" != "bin/restoreDatabase.sh" ]; then
	echo "Must be executed from project root"
	exit 1
fi

DIR=$(cd "$(dirname "$0")" && pwd)

echo "Cleaning database"
"$DIR"/mysql.sh < "$DIR"/../sla-repository/src/main/resources/sql/atossla.sql

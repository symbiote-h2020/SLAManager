#!/usr/bin/env bash
#
# To be executed from application root path
#

#####

# empty: debug disabled
DEBUG=

SLA_MANAGER_URL=${SLA_MANAGER_URL:-http://localhost:8080/api}

#####

shopt -s expand_aliases
alias curl='/usr/bin/curl -sLi '

function header() {
	echo
	echo
	echo "#### $1 #### "
}

debug() {
	[[ -n $DEBUG ]] && echo "$*" >&2
}

function curl_cmd() {
	[[ -n $DEBUG ]] && set -o xtrace
	curl -u user:password "$@"
	code=$?
	[[ -n $DEBUG ]] && set +o xtrace
	
	return $code
}

function curl_post() {
	#
	# $1: relative url
	# $2: file to send
	# $3: content type header
	# $4: accept header
	type_header=${3:+-H Content-type:$3}
	accept_header=${4:+-H Accept:$4}
	url="$SLA_MANAGER_URL/$1"
	out=$(curl_cmd "-d@$2" -X POST $type_header $accept_header "$url")
	code=$?
	debug $out
	filter_status=$(echo "$out" | grep -v "^HTTP\/1.1 100" | awk "/^HTTP\/1.1/ {print \$2;}" )
	filter_location=$(echo "$out" | grep -i "^location:" | sed -e "s/^location: *\(.*\)$/\1/i")
	debug "POST $url : $filter_status $filter_location"
	echo "$filter_status $filter_location"

	return $code
}

function curl_put() {
	# $1: relative url
	url="$SLA_MANAGER_URL/$1"
	out=$(curl_cmd -X PUT "$url")
	code=$?
	[[ -n $DEBUG ]] && echo "$out" >&2
	filter_status=$(echo "$out" | grep -v "^HTTP\/1.1 100" | awk "/^HTTP\/1.1/ {print \$2;}" )
	debug "PUT $url : $filter_status"
	echo "$filter_status"

	return $code
}

function check() {
	#$1 expected
	#$2 actual

	out=$(echo "$2" | grep "$1")
	if [ "$?" = "0" ]; then
		return $code
	else
		echo "ERROR: Expected: $1. Actual: $2"
		exit $code
	fi
}

function check_curl_post() {
	out=$(curl_post "$1" "$2" "$3" "$4")
	code=$?

	check "201" $out
}

function check_curl_put() {
	out=$(curl_put "$1" "$2" "$3" "$4")
	code=$?

	check "20." $out
}


echo serverurl=SLA_MANAGER_URL=$SLA_MANAGER_URL

header "Add provider-a xml" ####
check_curl_post "providers" "samples/provider-a.xml" "application/xml" "application/xml"


header "Add provider-b xml" ####
check_curl_post "providers" "samples/provider-b.xml" "application/xml" "application/xml"

header "Add template xml" ####
check_curl_post "templates" "samples/template-a.xml" "application/xml" "application/xml"

header "Add agreement xml" ####
check_curl_post "agreements" "samples/agreement-a.xml" "application/xml" "application/xml"

exit

header "Start enforcement agreement-a" ####
check_curl_put "enforcements/agreement-a/start"


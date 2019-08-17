#!/bin/bash

PARSED_OPTIONS=$(getopt -n "$0" -o p:e:w: -l path:extension:what: -- "$@")

#PARSED_OPTIONS=`getopt -n "$0" -o "p:e:w:" -l "path:extension:what:" -- "$@"`

#PARSED_OPTIONS=`getopt -o vhns: --long verbose,dry-run,help,stack-size: -n 'parse-options' -- "$@"`

#Checking whether getopt returned 0 code
if [ $? -ne 0 ];
then
  echo "error"
  exit 1
fi

echo "first argument is $1"

#Looks like the way to plug in getopt output back to native arguments of the script
eval set -- "$PARSED_OPTIONS"

echo "first argument is $1"

while [[ $# -gt 0 ]]; do
    case "$1" in
    -p|--path)
        path=$2
        shift
        ;;
    -e|--extension)
        extension=$2
        shift
        ;;
    -w|--what)
        what=$2
        shift
        ;;
    --)
        shift
        break
        ;;
    *)
        break
        ;;
    esac
    shift
done

echo "path is $path, extension is $extension, what is $what"
exit 0;

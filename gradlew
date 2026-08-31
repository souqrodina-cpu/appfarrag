#!/bin/sh
exec gradle "$@" 2>/dev/null || ./gradle/wrapper/gradle-wrapper.jar "$@"

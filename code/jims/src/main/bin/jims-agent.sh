#!/bin/bash
#############################################################
#
# jims-agent v@Version@, build: @Build@, @Date@
#
# JIMS - the JMX-based Infrastructure Monitoring System
#
# Main bash script file for starting JIMS agent
# See man page for details
#
# Author: Kazimierz Balos, kbalos[at]agh.edu.pl
#	  Marcin Jarzab, mj[at]agh.edu.pl
# Affiliation: Department of Computer Science
#              University of Science and Technology (AGH-UST) 
#              Krakow/POLAND
#
#############################################################

export JAVA_HOME=/usr/java
export JIMS_AGENT_OS_PROFILE=Crossbow

#set -x

DEBUG=false

Version=@Version@

# Setup JIMS_HOME
DIRNAME=`dirname $0`
if [ "$JIMS_HOME" = "" ]; then
    JIMS_HOME=`cd $DIRNAME/..; pwd`
fi

# Include script with defined functions
. $JIMS_HOME/bin/jims-functions.sh

JIMS_COMMAND=""
HOST_IP=""

if [ $# -eq 1 ] 
then
	if [ "$1" != "start" -a "$1" != "stop" -a "$1" != "restart" -a "$1" != "status" -a "$1" != "clean" -a "$1" != "clean-restart" -a "$1" != "cluster" ]
	then
		jims_help
		exit
	fi
	JIMS_COMMAND="$1"	
elif [ $# -eq 3 ]
then
	if [ "$1" != "-b" -a "$3" != "start" -a "$3" != "stop" -a "$3" != "restart" -a "$3" != "status" -a "$3" != "clean" -a "$3" != "clean-restart" -a "$3" != "cluster" ]
	then
		jims_help
		exit
	fi
	HOST_IP="$2"
	JIMS_COMMAND="$3" 
elif [ $# -eq 0 ]
then
	jims_help
	exit
fi

info "Starting JIMS v. $Version"

# Directory where Java libraries are stored
if [ "${JIMS_LIB}" = "" ]
then
	JIMS_LIB=$JIMS_HOME/lib
fi
info "JIMS_LIB=$JIMS_LIB"

# Check for JIMS agent startup class
if [ "${JIMS_AGENT_CLASS}" = "" ]; then
	JIMS_AGENT_CLASS=org.jims.agent.JIMSAgent
fi
info "JIMS_AGENT_CLASS=${JIMS_AGENT_CLASS}"

# Variable where JIMS flags are stored
JIMS_REGISTRY=0

# Constants indicating diffrent functionalities discovered in cluster
# Indicate that agent should load monitoring specific modules
JIMS_CONST_MON=1

# Indicate that agent should load gateway specific modules
JIMS_CONST_GW=2

# Indicate that agent should load http repository specific modules
JIMS_CONST_REP=4

# Indicate that agent should load Grid Engine (PBS or SGE) specific modules
JIMS_CONST_PBS=8
JIMS_CONST_SGE=16

# Indicate type of operating system
JIMS_OS_LI=false
JIMS_OS_SO=false
SYSTEM_OS=`uname`
if [ $SYSTEM_OS = "SunOS" ]
then
	JIMS_OS_SO=true
elif [ $SYSTEM_OS = "Linux" ]
then
	JIMS_OS_LI=true
fi

# Cluster functionality (is set up automatically when PBS or SGE is found)
CLUSTER=false
# Test whether SGE is supported
if [ "${SGE_ROOT}x" != "x" ]
then
	if [ -e ${SGE_ROOT}/bin ]
	then
		JIMS_REGISTRY=`expr $JIMS_REGISTRY + $JIMS_CONST_SGE`
		CLUSTER=true
		info "SGE: found"
	else
		info "SGE: not found"
	fi
else
	info "SGE: not found"
fi

# Test whether PBS is supported (only under Linux)
PBS=false
if [ "$JIMS_OS_LI" == "true" ]
then
	which pbsnodes 2>/dev/null 1>/dev/null
	if [ $? == 0 ]
	then
		JIMS_REGISTRY=`expr $JIMS_REGISTRY + $JIMS_CONST_PBS`
		PBS=true
		CLUSTER=true
		info "PBS: found"
	else
		info "PBS: not found"
	fi
	# Get the list of valid nodes from PBS
	if [ "$PBS" = "true" ]
	then
		VALID_NODES=""
		ALL_NODES=`pbsnodes -a | grep ^[a-zA-Z] | sed 's/\..*//'`
		STATES=`pbsnodes -a | grep state|sed 's/state = //g'`
		LIST_LENGTH=0
		STATES_LENGTH=0
		for NODE_ITEM in $ALL_NODES
		do
			LIST_TABLE[$LIST_LENGTH]=$NODE_ITEM
			LIST_LENGTH=`expr $LIST_LENGTH + 1`
		done
		for STATE_ITEM in $STATES
		do
			if [ ${STATE_ITEM} != down ]
			then
				VALID_NODES="$VALID_NODES ${LIST_TABLE[$STATES_LENGTH]}"
			fi
			STATES_LENGTH=`expr $STATES_LENGTH + 1`
		done
		LIST=$VALID_NODES
	else
		LIST=localhost
	fi
else
	info "PBS: not supported"
fi

# User name configuration for JIMS service
JIMS_USER=jims
# Do not edit, should be the same as $JIMS_USER
JIMS_GROUP=$JIMS_USER
# Type and permissions for JIMS log directory
JIMS_RIGHTS="drwxr-xr-x"
# Test if user exists
id $JIMS_USER 1>/dev/null 2>/dev/null
if [ $? -eq 0 ]
then
	info "JIMS user: "`id $JIMS_USER`
else
	error "the user $JIMS_USER does not exist."
fi

# Test whether running as $JIMS_USER user
if [ "${JIMS_USER}x" = "${USER}x" ]
then
	info "running service as $JIMS_USER"
else
	warn "running service as not $JIMS_USER user!"
fi

# Configure log and tmp directory
JIMS_LOG_DIR=$JIMS_HOME/var/jims/log
JIMS_TMP_DIR=$JIMS_HOME/var/jims/tmp
JIMS_DBRRD_DIR=$JIMS_HOME/var/jims/db/rrd

# Check the user, group and permissions for JIMS log and tmp directory
for i in $JIMS_LOG_DIR $JIMS_TMP_DIR $JIMS_DBRRD_DIR 
do
	configureDirectory $i $JIMS_USER $JIMS_GROUP $JIMS_RIGHTS
done

# Determine the gateway agent facility (GW) and repository address (HTTP)
# We also test whether the filesystem where JIMS is running is shared.
# Shared (site.cfg):
# GW   - name of the host acting as JIMS gateway agent
# HTTP - name of the host with JIMS modules
# Splitted (site.cfg):
# GW   - true/false (is gateway or not)
# HTTP - as above
# Determine file system table configuration file
if [ `uname` == "Linux" ]
then
	FS_FILE=fstab
elif [ `uname` == "SunOS" ]
then
	FS_FILE=vfstab
fi
HOMEDIR=`echo $HOME | sed "s@/$USER@@"`
HOMEDIR_PARSED=`echo $HOMEDIR | sed 's%/%_%g'`

SHARED_FS=`is_shared`
info "shared filesystem: $SHARED_FS"

# Read GW and HTTP value from JIMS configuration file
SITE_CONFIG_FILE=$JIMS_HOME/etc/jims/site.cfg
if [ "$CLUSTER" = "true" ]
then
	if [ ! -f $SITE_CONFIG_FILE ]
	then
		fatal "the site config file $SITE_CONFIG_FILE doesn't exist, exiting"
	else
		GW=`cat $SITE_CONFIG_FILE 2>&1 | grep ^GW | awk 'BEGIN{FS="="}{print $2}'`
		HTTP=`cat $SITE_CONFIG_FILE 2>&1 | grep ^HTTP | awk 'BEGIN{FS="="}{print $2}'`
	fi
else
	if [ ! -f $SITE_CONFIG_FILE ]
	then
		warn "the site config file $SITE_CONFIG_FILE doesn't exist, using defaults"
		GW=$HOSTNAME
		HTTP=$HOSTNAME
	else
		GW=`cat $SITE_CONFIG_FILE 2>&1 | grep ^GW | awk 'BEGIN{FS="="}{print $2}'`
		HTTP=`cat $SITE_CONFIG_FILE 2>&1 | grep ^HTTP | awk 'BEGIN{FS="="}{print $2}'`
	fi
fi

# Determine gateway facility
IS_GATEWAY=false
if [ "$SHARED_FS" == "true" ] 
then
	# On shared filesystem:
	# - computational nodes should compare GW with hostnames and not set GW facility
	# - access node should compare GW with its hostname and set GW facility
	if [ "$GW" == "$HOSTNAME" ]
	then
		# Set gateway facility for this agent
		JIMS_REGISTRY=`expr $JIMS_REGISTRY + $JIMS_CONST_GW`
		IS_GATEWAY=true
	fi
else
	# On not shared filesystem:
	# - computational nodes and access node should use logical value of GW
	# On shared filesystems:
	# - access node has not shared filesystem and should compare GW with hostname
	if [ "$GW" == "$HOSTNAME" -o "$GW" == "true" ]
	then
		# Set gateway facility for this agent
		JIMS_REGISTRY=`expr $JIMS_REGISTRY + $JIMS_CONST_GW`
		IS_GATEWAY=true
	fi
fi

# Determine HTTP repository facility
IS_REPOSITORY=false
if [ "$HTTP" == "$HOSTNAME" ]
then
	# Set HTTP repository facility for this agent
	JIMS_REGISTRY=`expr $JIMS_REGISTRY + $JIMS_CONST_REP`
	IS_REPOSITORY=true
fi

# Decide whether to monitor gateway node
GATEWAY_MONITORING=true
# Apply setting
if [ "${IS_GATEWAY}" == "true" ]
then
	if [ "${GATEWAY_MONITORING}" == "true" ]
	then
	# Set monitoring facility for this agent
	JIMS_REGISTRY=`expr $JIMS_REGISTRY + $JIMS_CONST_MON`
	fi
else
	# Set monitoring facility for this agent
	JIMS_REGISTRY=`expr $JIMS_REGISTRY + $JIMS_CONST_MON`
fi

# Obtaining IP address 
# The solution is resistant to many default interfaces
if [ "$HOST_IP" = "" ]
then
	if [ $SYSTEM_OS = "Linux" ]
	then
		DEFAULT_IFACE_LIST=`/sbin/route -n | grep "^0.0.0.0" | awk '{print $8}'`
		IFCOUNT=0
		for i in $DEFAULT_IFACE_LIST
		do
			IFCOUNT=`expr $IFCOUNT + 1`
		done
		if [ $IFCOUNT -eq 0 ]
		then
			fatal "Default interface not found, exiting"
		fi
		DEFAULT_IFACE=`for i in $DEFAULT_IFACE_LIST; do echo $i; break; done`
		info "interfaces: $IFCOUNT; taking the first one: $DEFAULT_IFACE"
		HOST_IP=`/sbin/ifconfig $DEFAULT_IFACE | grep "inet addr" | awk '{print $2}' | awk -F: '{print $2}'`
	elif [ $SYSTEM_OS = "SunOS" ]
	then
		# Command for Solaris (NO GNU) awk
		#HOST_IP=`cat /etc/hosts | awk "/[\t ]$HOSTNAME[\t ]/" | awk '{print $1}'`
		HOST_IP=`getent hosts $HOSTNAME | awk '{print $1}'`
	fi
fi

# End of environment testing part

# Test if JAVA_HOME defined
if [ "${JAVA_HOME}x" = "x" ]
then
	fatal "JAVA_HOME not set, exiting"
else
	if [ -e ${JAVA_HOME}/bin/java -a -e ${JAVA_HOME}/bin/jps ]
	then
		JAVA_VM="$JAVA_HOME/bin/java"
		JAVA_PS="$JAVA_HOME/bin/jps"
		info "JAVA_HOME=$JAVA_HOME"
	else
		fatal "JAVA_HOME incorrect - check installation and version (>=5.0)"
	fi
fi

# Decide wheter to monitor JVM and enable built-in JMX monitoring agent
ENABLE_JVM_MONITORING=false
JVM_MONITORING_PORT=10000
JVM_MONITORING_AUTHENTICATE=false
JVM_MONITORING_SSL=false
if [ "$ENABLE_JVM_MONITORING" == "true" ]
then
  JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote"
  JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.port=$JVM_MONITORING_PORT"
  JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.authenticate=$JVM_MONITORING_AUTHENTICATE"
  JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.ssl=$JVM_MONITORING_SSL"
fi
info "JVM Monitoring enabled: $ENABLE_JVM_MONITORING"
if [ "$ENABLE_JVM_MONITORING" == "true" ]
then
  info "JVM Monitoring port: $JVM_MONITORING_PORT"
  info "JVM Monitoring authenticate: $JVM_MONITORING_AUTHENTICATE"
  info "JVM Monitoring ssl: $JVM_MONITORING_SSL"
fi

# Common options for Java Virtual Machine:
# -server     to select the "server" VM
# -Xms<size>  set initial Java heap size
# -Xmx<size>  set maximum Java heap size
# -Xss<size>  set java thread stack size
# If JAVA_OPTS is not set try check for Hotspot
if [ "x$JAVA_OPTS" = "x" ] 
then
	# Check for SUN(tm) JVM w/ HotSpot support
	if [ "x$HAS_HOTSPOT" = "x" ]
	then
		HAS_HOTSPOT=`$JAVA_VM -version 2>&1 | grep -i HotSpot`
	fi
	# Enable -server if we have Hotspot, unless we can't
	if [ "x$HAS_HOTSPOT" != "x" ] 
	then
		JAVA_OPTS="$JAVA_OPTS -server"
	fi
fi

JAVA_OPTS="$JAVA_OPTS -Djims_home=${JIMS_HOME}"
JAVA_OPTS="$JAVA_OPTS -Djava.library.path=$JIMS_TMP_DIR"

# 1. Classloader used for complex types
# 2. Codebase for RMI class loader
# 3. Very important in multi-interface systems:
# 	-Causes to create remote object references with given host IP. The IP number is also used 
#	by discovery services for finding apropriate interface number.
JAVA_OPTS="$JAVA_OPTS -Djava.rmi.server.RMIClassLoaderSpi=org.jims.agent.JIMSAgentRMIClassLoaderSpi"
JAVA_OPTS="$JAVA_OPTS -Djava.rmi.server.codebase=http://${HTTP}:7702/"
JAVA_OPTS="$JAVA_OPTS -Djava.rmi.server.hostname=$HOST_IP"

# Security manager setup
JAVA_OPTS="$JAVA_OPTS -Djava.security.manager=java.rmi.RMISecurityManager"
JAVA_OPTS="$JAVA_OPTS -Djava.security.policy=${JIMS_HOME}/etc/jims/jims.policy"

# If we have IPv6 support switch to IPv4
JAVA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true"

# Java util.logging configuration
JAVA_OPTS="$JAVA_OPTS -Djava.util.logging.config.file=${JIMS_HOME}/etc/jims/logging.properties"

info "JAVA_VM=$JAVA_VM"
info "JAVA_PS=$JAVA_PS"
i=0
for JAVA_OPTION in $JAVA_OPTS
do
	info "JAVA_OPT[$i]=$JAVA_OPTION"
	i=`expr $i + 1`
done

if [ "$JIMS_COMMAND" = "start" -o "$1" = "$JIMS_COMMAND" ]
then
	if [ "$IS_REPOSITORY" = "true" ]
	then
		info "installing mlet files:"
		# Configure HTTP server root directory
		MLET_ROOT=$JIMS_TMP_DIR/mletroot
		# Create the folder if necessary
		if [ ! -d $MLET_ROOT ]
		then
			info "$MLET_ROOT does not exist, creating..."
			mkdir -p $MLET_ROOT
			chmod 755 $MLET_ROOT
		fi
		info "loading files to $MLET_ROOT"
		loadFiles $JIMS_HOME/etc/jims mlet
		loadFiles $JIMS_LIB jar
		loadFiles $JIMS_HOME/share/java jar
	fi
fi

#Set classpath for JIMS agent  
#Some extra JARS might be set in parent scripts
if [ $JIMS_REGISTRY -gt 1 ]
then
	JAR_LIST="$JAR_LIST saaj-api-2.1.3.jar saaj-impl-2.1.3.jar jmxws-ea3.0.0.jar xercesImpl-2.8.1.jar activation-2.1.3.jar http-2.1.3.jar jaxb-impl-2.1.3.jar jaxws-api-2.1.3.jar jaxws-tools-2.1.3.jar jsr181-api-2.1.3.jar resolver-2.1.3.jar FastInfoset-2.1.3.jar jaxb-api-2.1.3.jar jaxb-xjc-2.1.3.jar jaxws-rt-2.1.3.jar jsr173_api-2.1.3.jar jsr250-api-2.1.3.jar sjsxp-2.1.3.jar wiseman-core-1.0.0.jar streambuffer-2.1.3.jar stax-ex-2.1.3.jar jna-3.2.7.jar"
fi

amount=0
all_amount=0
for i in $JAR_LIST 
do
	if [ "${CLASSPATH}" == "" ]
	then
		JAR_FILE=$JIMS_LIB/$i
		if [ -e $JAR_FILE ]
		then
			info "CLASSPATH[$i]: OK"
			debug "adding $i to the CLASSPATH variable"
			amount=`expr $amount + 1`
			CLASSPATH=$JIMS_LIB/$i
		else
			warn "CLASSPATH[$i]: Not installed"
    fi
    all_amount=`expr $all_amount + 1`
   else
		JAR_FILE=$JIMS_LIB/$i
		if [ -e $JAR_FILE ]
		then
			info "CLASSPATH[$i]: OK"
			debug "adding $i to the CLASSPATH variable"
			amount=`expr $amount + 1`
			CLASSPATH=$CLASSPATH:$JIMS_LIB/$i
		else
			warn "CLASSPATH[$i]: Not installed"
		fi
		all_amount=`expr $all_amount + 1`
	fi
done
info "CLASSPATH JARs: [$amount/$all_amount]"

# Set CLASSPATH to JIMS core jars
for i in `ls $JIMS_HOME/core/lib/*.jar 2>/dev/null`
do
	CLASSPATH=$CLASSPATH:$i
done

# Jar with HTTP Server for repository service, must be present in classpath
if [ "${IS_REPOSITORY}" == "true" ]
then
	CLASSPATH=$CLASSPATH:$JIMS_HOME/share/java/jims-httpserver-3.0.0.jar
fi

echo "CLASSPATH: $CLASSPATH"

JIMS_AGENT_PID=$JIMS_LOG_DIR/agent-$HOST_IP.pid

# Evaluated parameters:
info "HOSTNAME:				$HOSTNAME($HOST_IP, $SYSTEM_OS)"
info "HTTP:					$HTTP(is repository: $IS_REPOSITORY)"
info "GW:					$GW(is gateway: $IS_GATEWAY)"
info "CLUSTER:				$CLUSTER"
info "JIMS_HOME:			$JIMS_HOME"
info "JIMS_REGISTRY: 		$JIMS_REGISTRY"
info "JIMS_AGENT_OS_PROFILE:$JIMS_AGENT_OS_PROFILE"
info "JIMS_GW_OS_PROFILE:	$JIMS_GW_OS_PROFILE"
info "[monitoring] =		$JIMS_CONST_MON"
info "[gateway   ] =		$JIMS_CONST_GW"
info "[repository] =		$JIMS_CONST_REP"
info "[pbs       ] =		$JIMS_CONST_PBS"
info "[sge       ] =		$JIMS_CONST_SGE"
debug "[linux     ] =		$JIMS_OS_LI"
debug "[sunos     ] =		$JIMS_OS_SO"

case $JIMS_COMMAND in
start)
	jims_start
;;
stop)
	jims_stop
;;
restart)
	jims_stop
	sleep 3 
	jims_start
;;
clean-restart)
  $0 stop
    sleep 1
  $0 clean
  $0 start
;;
status)
	jims_status
;;
clean)
	jims_clean
;;
cluster)
	jims_cluster
;;
*)
	jims_help
;;
esac

#############################################################
#
# End of JIMS bash script file (jims-agent)
#
#############################################################

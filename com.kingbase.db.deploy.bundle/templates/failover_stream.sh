#!/bin/sh
#source /home/kdb/KingbaseES/V8/kbrc
# %H hostname of the new master node
new_master=$1
# %h = host name
old_primary=$4
# %P = old primary node id
old_primary_node=$2
# %d = node id    (if node id is primary goes down, then we will ssh -o StrictHostKeyChecking=no -T $new_master that standby takes over primary node.)
failed_node=$3
# %O = old_primary host
old_primary_host=$5

#set kingbase PATH
KB_PATH="/opt/KingbaseES/V8/bin"
#CLUSTER_BIN_PATH="${KB_PATH}"
#set kingbase LD_LIBRARY_PATH 
KB_LD_PATH="/opt/KingbaseES/V8/lib"
#CLUSTER_LIB_PATH="${KB_LD_PATH}"
#set kingbase data path
KB_DATA_PATH="/opt/KingbaseES/V8/data"
#set kingbase data user
KB_USER="SYSTEM"
#set kingbase data name 
KB_DATANAME="TEST"
#set kingbase data port
KB_PORT="55433"
#set kingbase system user execute the program
KB_EXECUTE_SYS_USER="lx"

#Is need real kb nodes require virtual IP, like x.x.x.x/24
KB_VIRTUAL_IP=""
#WARING, MUST CHANGE THE SAME NAME THAT ALL THE NETWORK CARDS, IF NOT ,DON'T ALTER
KB_VIRTUAL_DEV_NAME=""

#SET all KingbaseES IP, new version not use
#ALL_KBIP=(192.168.8.22 192.168.8.23 192.168.8.25)





#SET FAILOVER_LOG_DIR="/tmp/failover.log"
FAILOVER_LOG_DIR="/tmp/failover.log"
ENDLS=$FAILOVER_LOG_DIR 2>&1

#SET THE PRIMARY FLAG NAME FILE
KB_PRIMARY_FLAG="KB_PRIMARY_FLAG"

function LOGFILE()
{
        SHELLNAME=$1
        fileindex=0
        echo `date +'%Y-%m-%d %H:%M:%S'` [$SHELLNAME] beging.. >> $FAILOVER_LOG_DIR 2>&1
        $SHELLNAME   >> $RECOVERY_LOG_DIR 2>&1
        
#        while [ 0 -ne ${#LOGDETAIL[*]} ]
#        do
#                #fileindex=0
#
#                echo ${LOGDETAIL[fileindex]} >> $FAILOVER_LOG_DIR 2>&1
#                unset LOGDETAIL[fileindex]
#                let fileindex+=1;
#        done
}

echo -----------------`date +'%Y-%m-%d %H:%M:%S'` failover beging---------------------------------------  >> $FAILOVER_LOG_DIR 2>&1
#echo `date +'%Y-%m-%d %H:%M:%S'` failover beging.. >> $FAILOVER_LOG_DIR 2>&1

#get the all node id (include primary id , not use primary)
#export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$CLUSTER_LIB_PATH
#export PATH=$PATH:$CLUSTER_BIN_PATH
#
#ksql -p 9999 -U ABC -W 123 -d TEST -h 192.168.8.177 -c "show pool_nodes;" | grep '[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}' | awk -F'|' '{print $1 $2}' >> $FAILOVER_LOG_DIR 2>&1


echo "----failover-stats is %H = hostname of the new master node [$new_master], %P = old primary node id [$2], %d = node id[$3], %h = host name [$4], %O = old primary host[$5] %m = new master node id [$6], %M = old master node id [$7], %D = database cluster path [$8]." >> $FAILOVER_LOG_DIR 2>&1

function SETFLAG()
{
    PRIMARYIP=${new_master}
    /usr/bin/ssh -o StrictHostKeyChecking=no -l $KB_EXECUTE_SYS_USER -T $new_master ": >  ${KB_DATA_PATH}/${KB_PRIMARY_FLAG} " >> $FAILOVER_LOG_DIR 2>&1
    for item in ${ALL_KBIP[*]}
    do

            echo $item

            if [ "$item"x = "$PRIMARYIP"x ]
            then
            /usr/bin/ssh -o StrictHostKeyChecking=no -l $KB_EXECUTE_SYS_USER -T $new_master "echo $item primary >>  ${KB_DATA_PATH}/${KB_PRIMARY_FLAG} " >> $FAILOVER_LOG_DIR 2>&1
            else
            /usr/bin/ssh -o StrictHostKeyChecking=no -l $KB_EXECUTE_SYS_USER -T $new_master "echo $item 1 >>  ${KB_DATA_PATH}/${KB_PRIMARY_FLAG} " >> $FAILOVER_LOG_DIR 2>&1
            fi

    done
}


if [ $old_primary_node -eq $failed_node ];then
# master down
echo "master down, let $new_master become new primary....."  >> $FAILOVER_LOG_DIR 2>&1
if [ "$KB_VIRTUAL_IP"x != ""x ]
then
    echo " `date +'%Y-%m-%d %H:%M:%S'` del old primary VIP" >> $FAILOVER_LOG_DIR 2>&1
    #LOGDETAIL=(`/usr/bin/ssh -o StrictHostKeyChecking=no -T $old_primary "$KB_PATH/change_vip.sh $KB_VIRTUAL_IP $KB_VIRTUAL_DEV_NAME del 2>&1"`)
    /usr/bin/ssh -o StrictHostKeyChecking=no -T $old_primary "$KB_PATH/change_vip.sh $KB_VIRTUAL_IP $KB_VIRTUAL_DEV_NAME del 2>&1" >> $FAILOVER_LOG_DIR 2>&1
    #LOGFILE DELVIP
    echo "`date +'%Y-%m-%d %H:%M:%S'` add VIP" >> $FAILOVER_LOG_DIR 2>&1
    /usr/bin/ssh -o StrictHostKeyChecking=no -T $new_master "$KB_PATH/change_vip.sh $KB_VIRTUAL_IP $KB_VIRTUAL_DEV_NAME add 2>&1" >> $FAILOVER_LOG_DIR 2>&1
fi


echo "`date +'%Y-%m-%d %H:%M:%S'` promote begin...let $new_master become master" >> $FAILOVER_LOG_DIR 2>&1
/usr/bin/ssh -o StrictHostKeyChecking=no -l $KB_EXECUTE_SYS_USER -T $new_master "$KB_PATH/kingbase_promote.sh $KB_PATH $KB_LD_PATH $KB_USER $KB_DATANAME $KB_PORT $KB_DATA_PATH 2>&1" >> $FAILOVER_LOG_DIR 2>&1

echo "`date +'%Y-%m-%d %H:%M:%S'` sync to async" >> $FAILOVER_LOG_DIR 2>&1
/usr/bin/ssh -o StrictHostKeyChecking=no -l $KB_EXECUTE_SYS_USER -T $new_master "$KB_PATH/sync_async.sh $KB_PATH $KB_LD_PATH $KB_DATA_PATH 2>&1" >> $FAILOVER_LOG_DIR 2>&1

echo "`date +'%Y-%m-%d %H:%M:%S'` make checkpoint" >> $FAILOVER_LOG_DIR 2>&1
/usr/bin/ssh -o StrictHostKeyChecking=no -l $KB_EXECUTE_SYS_USER -T $new_master "$KB_PATH/kingbase_checkpoint.sh $KB_PATH $KB_LD_PATH $KB_USER $KB_DATANAME $KB_PORT 2>&1" >> $FAILOVER_LOG_DIR 2>&1

#echo "`date +'%Y-%m-%d %H:%M:%S'` attach primary flag! important" >> $FAILOVER_LOG_DIR 2>&1
#/usr/bin/ssh -o StrictHostKeyChecking=no -l $KB_EXECUTE_SYS_USER -T $new_master "echo 1 >  ${KB_DATA_PATH}/${KB_PRIMARY_FLAG} " >> $FAILOVER_LOG_DIR 2>&1
    #SETFLAG

else

echo "standby down, master still $old_primary_host" >> $FAILOVER_LOG_DIR 2>&1
echo "`date +'%Y-%m-%d %H:%M:%S'` sync to async" >> $FAILOVER_LOG_DIR 2>&1
/usr/bin/ssh -o StrictHostKeyChecking=no -l $KB_EXECUTE_SYS_USER -T $old_primary_host "$KB_PATH/sync_async.sh $KB_PATH $KB_LD_PATH $KB_DATA_PATH 2>&1" >> $FAILOVER_LOG_DIR 2>&1

#echo "`date +'%Y-%m-%d %H:%M:%S'` attach primary flag! important" >> $FAILOVER_LOG_DIR 2>&1
#/usr/bin/ssh -o StrictHostKeyChecking=no -l $KB_EXECUTE_SYS_USER -T $new_master "echo 1 >  ${KB_DATA_PATH}/${KB_PRIMARY_FLAG} " >> $FAILOVER_LOG_DIR 2>&1
    #SETFLAG
    
# recover action old master to new standby by hand until network back; to_standby.sh
fi


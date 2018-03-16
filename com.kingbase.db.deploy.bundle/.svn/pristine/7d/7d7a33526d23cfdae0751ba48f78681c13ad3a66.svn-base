#SET opposite/localhost IP, must be set
KB_GATEWAY_IP=""
KB_LOCALHOST_IP=""
#SET SYNC_FLAG, default ASYNC(0), SYNC(1)  
SYNC_FLAG=0
#SET DEL  VIP , if use
KB_VIP="192.168.8.177"
KB_REAL_DEV="eth0"

#SET THE POOL VIP, option, can be attach the pool
KB_POOL_VIP=""

#POOL default, listen port and pcp port
KB_POOL_PORT=9999
KB_POOL_PCP_PORT=9898
#PCP CONF , can attach node
PCP_USER="help"
PCP_PASS="me"

#set kingbase etc path, save recovery.done file
KB_ETC_PATH="/home/kdb/KingbaseES/V8/etc"
#set kingbase PATH
KB_PATH="/opt/KingbaseES/V8/bin"
#set kingbase LD_LIBRARY_PATH 
KB_LD_PATH="/opt/KingbaseES/V8/lib"
#set cluster PATH to use pcp_* command
CLUSTER_BIN_PATH="/opt/KingbaseES/V8/Cluster/bin"
CLUSTER_LIB_PATH="/opt/KingbaseES/V8/Cluster/lib"
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


#SET RECOVERY_LOG_DIR="/tmp/recovery.log"
RECOVERY_LOG_DIR="/tmp/recovery.log"
ENDLS=$RECOVERY_LOG_DIR 2>&1

#SET THE PRIMARY FLAG NAME FILE
KB_PRIMARY_FLAG="KB_PRIMARY_FLAG"

#SET MYSELF RECOVERY FLAG NAME FILE
KB_RECOVERY_FLAG="KB_RECOVERY_FLAG"
#SET POOL STATU IN FILE
KB_CLUSTER_STATUS="/tmp/pool_nodes"

function LOGFILE()
{
        SHELLNAME=$1
        fileindex=0
        echo `date +'%Y-%m-%d %H:%M:%S'` $SHELLNAME beging.. >> $RECOVERY_LOG_DIR 2>&1
        while [ 0 -ne ${#LOGDETAIL[*]} ]
        do
                #fileindex=0

                echo ${LOGDETAIL[fileindex]} >> $RECOVERY_LOG_DIR 2>&1
                unset LOGDETAIL[fileindex]
                let fileindex+=1;
        done
}



export PATH=$KB_PATH:$CLUSTER_BIN_PATH:$PATH
export LD_LIBRARY_PATH=$KB_LD_PATH:$CLUSTER_LIB_PATH:$LD_LIBRARY_PATH


#AUTO RECOVERY
#STOPED AND PING act

AUTO_FLAG=0
PING_TIMES=3

echo "---------------------------------------------------------------------" >> $RECOVERY_LOG_DIR 2>&1
echo `date +'%Y-%m-%d %H:%M:%S'` recover beging... >> $RECOVERY_LOG_DIR 2>&1


#if network down ,exit
result=`ping $KB_GATEWAY_IP -c $PING_TIMES | grep received | awk '{print $4}'`
if [ $result -eq $PING_TIMES ]
then
    #AUTO_FLAG=`/usr/bin/ssh -o StrictHostKeyChecking=no -T $KB_GATEWAY_IP "cat ${KB_DATA_PATH}/${KB_PRIMARY_FLAG} |head -n 1"`
    
    #new method. use myself FLAG
    if [ -f ${KB_DATA_PATH}/${KB_RECOVERY_FLAG} ];then
        IMRECOVERY_FLAG=`cat ${KB_DATA_PATH}/${KB_RECOVERY_FLAG} |head -n 1`
        if [ "$IMRECOVERY_FLAG"x = "1"x ]
        then
            echo "I'm already recovery now, return noting to do ." >> $RECOVERY_LOG_DIR 2>&1
            exit 0;
        fi

    fi
    
    #write pool nodes in file 
    ksql -p $KB_POOL_PORT -U $KB_USER  -d $KB_DATANAME -h $KB_POOL_VIP  -c "show pool_nodes;" 2>&1 > $KB_CLUSTER_STATUS
    
    #NOWPRIMARYIP=`ksql -p $KB_POOL_PORT -U $KB_USER  -d $KB_DATANAME -h $KB_POOL_VIP  -c "show pool_nodes;" | grep primary | grep -v grep |awk -F'|' '{print $2}'`
    
    NOWPRIMARYIP=`cat $KB_CLUSTER_STATUS | grep primary | grep -v grep |awk -F'|' '{print $2}'`
    if [ "$NOWPRIMARYIP"x = ""x ]
    then
        echo "no primary node in kingbasecluster, nothing to do " >> $RECOVERY_LOG_DIR 2>&1
        exit 0;
    else
        IMPRI_FLAG=`echo $NOWPRIMARYIP | grep -w $KB_LOCALHOST_IP | wc -l`
        if [ "$IMPRI_FLAG"x = "1"x ];then
            echo "I,m node is primary, nothing to do " >> $RECOVERY_LOG_DIR 2>&1
            exit 0;
        fi
        
        #NEED_CHANGE=`cat ${KB_DATA_PATH}/recovery.conf | grep "host=${NOWPRIMARYIP// }" | wc -l`
        NEED_CHANGE=`cat ${KB_DATA_PATH}/recovery.conf | grep $NOWPRIMARYIP | wc -l`
        IMSTATUS=`cat $KB_CLUSTER_STATUS | grep down | grep -w $KB_LOCALHOST_IP| grep -v grep | wc -l `
        if [ "$NEED_CHANGE"x = "0"x -o "$IMSTATUS"x = "1"x ]
        then
            echo "primary node/Im node status is changed, pri[$NOWPRIMARYIP], I,m status is [$IMSTATUS], I will be in recovery. " >> $RECOVERY_LOG_DIR 2>&1
            cat $KB_CLUSTER_STATUS >> $RECOVERY_LOG_DIR 2>&1
            echo "set flag 1 , other program do not interrupt me " >> $RECOVERY_LOG_DIR 2>&1
            echo 1 > ${KB_DATA_PATH}/${KB_RECOVERY_FLAG}
        else
            echo "no status changed ,nothing to do ." >> $RECOVERY_LOG_DIR 2>&1
            exit 0;
        fi
    fi
    
    
#    IMRECOVERY_FLAG=`/usr/bin/ssh -o StrictHostKeyChecking=no -T $NOWPRIMARYIP "cat ${KB_DATA_PATH}/${KB_PRIMARY_FLAG} | grep $KB_LOCALHOST_IP  |awk '{print \\$2}'"`
#    
#    if [ "$IMRECOVERY_FLAG"x = "1"x ]
#    then
#    echo "set flag 0 , other program do not interrupt me " >> $RECOVERY_LOG_DIR 2>&1
#    ssh -o StrictHostKeyChecking=no -T $NOWPRIMARYIP "sed -i \"s#${KB_LOCALHOST_IP} 1#${KB_LOCALHOST_IP} 0#g\" ${KB_DATA_PATH}/${KB_PRIMARY_FLAG} " >> $RECOVERY_LOG_DIR 2>&1
#    
#    else
#    echo "no status changed ,nothing to do ." >> $RECOVERY_LOG_DIR 2>&1
#    exit 0;
#    
#    fi
    
#    if [ $AUTO_FLAG -eq 1 ]
#    then 
#        /usr/bin/ssh -o StrictHostKeyChecking=no  -T $KB_GATEWAY_IP "echo 0 >  ${KB_DATA_PATH}/${KB_PRIMARY_FLAG} " 
#        echo "set flag 0 , other program do not interrupt me " >> $RECOVERY_LOG_DIR 2>&1
#    else
#        echo "but nothing to do ." >> $RECOVERY_LOG_DIR 2>&1
#        exit 0;
#    fi

else
    
    echo "PING $KB_GATEWAY_ROUTE FAILD" >> $RECOVERY_LOG_DIR 2>&1
    echo "but nothing to do ." >> $RECOVERY_LOG_DIR 2>&1
    exit 0;
fi


    #del vip
    #echo "PING $KB_GATEWAY_ROUTE FAILD" >> $RECOVERY_LOG_DIR 2>&1
    if [ "$KB_VIP"x != ""x ]
    then
        echo "`date +'%Y-%m-%d %H:%M:%S'` now will del vip [$KB_VIP]" >> $RECOVERY_LOG_DIR 2>&1
        vipnum=`ssh -o StrictHostKeyChecking=no -l root -T localhost "/sbin/ip addr | grep -w "$KB_VIP" | wc -l"`
        if [ $vipnum -eq 1 ]
        then
            echo "excute [/sbin/ip addr del $KB_VIP dev $KB_REAL_DEV ]" >> $RECOVERY_LOG_DIR 2>&1
            ssh -o StrictHostKeyChecking=no -l root -T localhost "/sbin/ip addr del $KB_VIP dev $KB_REAL_DEV" >> $RECOVERY_LOG_DIR 2>&1
        else
            echo "but no $KB_VIP on my DEV, nothing to do with del" >> $RECOVERY_LOG_DIR 2>&1
        fi
        
    fi




#kingbase_pid=`cat ${KB_DATA_PATH}/kingbase.pid |head -n 1`
#
#if [ "$kingbase_pid"x != ""x ]
#then
#    kingbase_exist=`ps -ef | grep $kingbase_pid | grep -v grep | wc -l`
#    if [ "$kingbase_exist" -eq 0 ]
#    then
#        #echo `date` kingbase is not alive! >> $RECOVERY_LOG_DIR 2>&1
#        echo "status down , now I'm must be a standby  " >> $RECOVERY_LOG_DIR 2>&1
#        result=`ping $KB_GATEWAY_ROUTE -c $PING_TIMES | grep received | awk '{print $4}'`
#        if [ $result -eq $PING_TIMES ]
#        then
#            result=`ping $KB_GATEWAY_IP -c $PING_TIMES | grep received | awk '{print $4}'`
#            if [ $result -eq $PING_TIMES ]
#            then
#            AUTO_FLAG=1
#            echo "set auto flag 1 , down and connect the primary" >> $RECOVERY_LOG_DIR 2>&1
#            else
#            echo "dont connect $KB_GATEWAY_IP" >> $RECOVERY_LOG_DIR 2>&1
#            exit 0 
#            fi
#        else 
#        echo "dont connect $KB_GATEWAY_ROUTE" >> $RECOVERY_LOG_DIR 2>&1
#        exit 0
#        fi
#     else   
#        echo "don't down ,but net work may be not active, let check"  >> $RECOVERY_LOG_DIR 2>&1
#        result=`ping $KB_GATEWAY_ROUTE -c $PING_TIMES | grep received | awk '{print $4}'`
#        if [ $result -eq $PING_TIMES ]
#        then
#            OPP_kingbase_pid=`ssh -o StrictHostKeyChecking=no -T $KB_GATEWAY_IP "cat ${KB_DATA_PATH}/kingbase.pid |head -n 1"`
#            sleep 1
#            linkstat=`ssh -o StrictHostKeyChecking=no -T $KB_GATEWAY_IP "ps -ef | grep sender | grep $OPP_kingbase_pid | grep -v grep |wc -l"`
#            sleep 1
#            if [ $linkstat -eq 0 ]
#            then
#            AUTO_FLAG=1
#            echo "set auto flag 1 , not connect the primary" >> $RECOVERY_LOG_DIR 2>&1
#            fi
#        fi
#          
#
#    fi
#else
#    echo "status down , now I'm must be a standby  " >> $RECOVERY_LOG_DIR 2>&1
#    result=`ping $KB_GATEWAY_ROUTE -c $PING_TIMES | grep received | awk '{print $4}'`
#    if [ $result -eq $PING_TIMES ]
#    then
#        result=`ping $KB_GATEWAY_IP -c $PING_TIMES | grep received | awk '{print $4}'`
#        if [ $result -eq $PING_TIMES ]
#        then
#        AUTO_FLAG=1
#        echo "set auto flag 1 , down and not connect the primary" >> $RECOVERY_LOG_DIR 2>&1
#        else
#        echo "dont connect $KB_GATEWAY_IP" >> $RECOVERY_LOG_DIR 2>&1
#        exit 0 
#        fi
#    else 
#    echo "dont connect $KB_GATEWAY_ROUTE" >> $RECOVERY_LOG_DIR 2>&1
#    exit 0
#    fi
#fi
#
#
#if [ $AUTO_FLAG -eq 0 ]
#then
#exit 0
#fi



#1, if recover node up, let it down , for rewind
kingbase_pid=`cat ${KB_DATA_PATH}/kingbase.pid |head -n 1`

if [ "$kingbase_pid"x != ""x ]
then
        kingbase_exist=`ps -ef | grep -w $kingbase_pid | grep -v grep | wc -l` 
        if [ "$kingbase_exist" -ge 1 ]
        then
            echo "`date +'%Y-%m-%d %H:%M:%S'` stop the kingbase"
            sys_ctl -D $KB_DATA_PATH stop -m i >> $RECOVERY_LOG_DIR 2>&1 &
            #echo set $KB_DATA_PATH down now...
            echo "set $KB_DATA_PATH down now..." >> $RECOVERY_LOG_DIR 2>&1 
            sleep 5
            echo "wait kb stoped 5 sec ......." >> $RECOVERY_LOG_DIR 2>&1 
            isstillalive=`ps -ef | grep -w $kingbase_pid |grep -v grep |wc -l`
            if [ $isstillalive -ne 0 ]
            then
                echo "need to killed ,is alived $isstillalive, let [ps -ef| grep -w $kingbase_pid | grep -v grep |awk '{print $2}' |xargs kill -9]" >> $RECOVERY_LOG_DIR 2>&1
                `ps -ef| grep -w $kingbase_pid | grep -v grep |awk '{print $2}' |xargs kill -9 >>  $RECOVERY_LOG_DIR 2>&1` 
                echo "kill after ...then check " >> $RECOVERY_LOG_DIR 2>&1 
                ps -ef | grep -w $kingbase_pid |grep -v grep >> $RECOVERY_LOG_DIR 2>&1 
            fi
        fi
fi


#2, sys_rewind
#LOGDETAIL=(`sys_rewind  --target-data=$KB_DATA_PATH --source-server="host=$KB_GATEWAY_IP port=$KB_PORT user=$KB_USER dbname=$KB_DATANAME" 2>&1`)
echo "`date +'%Y-%m-%d %H:%M:%S'` sys_rewind..." >> $RECOVERY_LOG_DIR 2>&1
sys_rewind  --target-data=$KB_DATA_PATH --source-server="host=$NOWPRIMARYIP port=$KB_PORT user=$KB_USER dbname=$KB_DATANAME" >> $RECOVERY_LOG_DIR 2>&1
sleep 2

#3, sed conf change #synchronous_standby_names

echo "`date +'%Y-%m-%d %H:%M:%S'` file operate" >> $RECOVERY_LOG_DIR 2>&1
cp ${KB_ETC_PATH}/kingbase.conf ${KB_DATA_PATH}/kingbase.conf
sed -i 's/synchronous_standby_names/#synchronous_standby_names/' $KB_DATA_PATH/kingbase.conf

#4, MV recovery.conf if old primary, but also used in old standby
cp ${KB_ETC_PATH}/recovery.done ${KB_DATA_PATH}/recovery.conf 
echo "cp recovery.conf..." >> $RECOVERY_LOG_DIR 2>&1

#WARING : IMPORTANT ,  SYNC OR ASYNC
#5, 
if [ $SYNC_FLAG -eq 1 ];then
# SYNC MODE
echo "SYNC RECOVER MODE"
#LOGDETAIL=(`/usr/bin/ssh -o StrictHostKeyChecking=no -l $KB_EXECUTE_SYS_USER -T $KB_GATEWAY_IP "$KB_PATH/sync_async.sh $KB_PATH $KB_LD_PATH $KB_DATA_PATH $SYNC_FLAG" 2>&1`)
#LOGFILE "/usr/bin/ssh -o StrictHostKeyChecking=no -l $KB_EXECUTE_SYS_USER -T $KB_GATEWAY_IP "$KB_PATH/sync_async.sh $KB_PATH $KB_LD_PATH $KB_DATA_PATH $SYNC_FLAG" 2>&1"
echo "`date +'%Y-%m-%d %H:%M:%S'` remote primary node change sync" >> $RECOVERY_LOG_DIR 2>&1
/usr/bin/ssh -o StrictHostKeyChecking=no -l $KB_EXECUTE_SYS_USER -T $NOWPRIMARYIP "$KB_PATH/sync_async.sh $KB_PATH $KB_LD_PATH $KB_DATA_PATH $SYNC_FLAG" >> $RECOVERY_LOG_DIR 2>&1
sleep 1

echo "SYNC RECOVER MODE..." >> $RECOVERY_LOG_DIR 2>&1
else
#ASYNC MODE
echo "ASYNC RECOVER MODE" >> $RECOVERY_LOG_DIR 2>&1
#noting to do
fi

#6, change recovery.conf ip -> primary.ip
NEED_CHANGE=`cat ${KB_DATA_PATH}/recovery.conf | grep "host=$NOWPRIMARYIP" | wc -l`
if [ "$NEED_CHANGE"x = "0"x ]
then
    echo "`date +'%Y-%m-%d %H:%M:%S'` change recovery.conf" >> $RECOVERY_LOG_DIR 2>&1
    sed -i "s#[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}#$NOWPRIMARYIP#g" ${KB_DATA_PATH}/recovery.conf >> $RECOVERY_LOG_DIR 2>&1
else
    echo "`date +'%Y-%m-%d %H:%M:%S'` no need change recovery.conf, primary node is $NOWPRIMARYIP" >> $RECOVERY_LOG_DIR 2>&1
    
fi



#7 start up
#LOGDETAIL=(`sys_ctl -D $DATA_DIR start 2>&1`)
echo "`date +'%Y-%m-%d %H:%M:%S'` start up the kingbase..." >> $RECOVERY_LOG_DIR 2>&1
sys_ctl -D $KB_DATA_PATH start >> $RECOVERY_LOG_DIR 2>&1 &
sleep 3
while [ 1 ]  
do
       
       echo "ksql -p $KB_PORT -U $KB_USER -d $KB_DATANAME   -c \"select 33333;\" "
       ksql -p $KB_PORT -U $KB_USER -d $KB_DATANAME   -c "select 33333;" >> $RECOVERY_LOG_DIR 2>&1
       rightnum=`ksql -p $KB_PORT -U $KB_USER -d $KB_DATANAME   -c "select 33333;" | grep 33333 | wc -l`
        if [ "$rightnum"x = "1"x ]
        then
                break
        else
                echo start standby query detail[$rightnum] , try again! 2>&1 >> $RECOVERY_LOG_DIR 2>&1
                sleep 3
                continue
        fi

done


echo "`date +'%Y-%m-%d %H:%M:%S'` start up standby successful!" >> $RECOVERY_LOG_DIR 2>&1

#7 attach pool

echo "`date +'%Y-%m-%d %H:%M:%S'` attach pool..." >> $RECOVERY_LOG_DIR 2>&1
if [ "$KB_POOL_VIP"x != ""x ]
then
    #ksql -p $KB_POOL_PORT -U $KB_USER   -d $KB_DATANAME -h $KB_POOL_VIP -c "show pool_nodes;"  >> $RECOVERY_LOG_DIR 2>&1
    #sleep 1
    #IM_NODE=`ksql -p $KB_POOL_PORT -U $KB_USER  -d $KB_DATANAME -h $KB_POOL_VIP  -c "show pool_nodes;" | grep down | grep $KB_LOCALHOST_IP| grep -v grep |awk '{print $1}'`
    IM_NODE=`cat $KB_CLUSTER_STATUS  | grep $KB_LOCALHOST_IP| grep -v grep |awk '{print $1}'`
    sleep 1
    
    if [ "$IM_NODE"x != ""x ]
    then
        echo "IM Node is $IM_NODE, will try [pcp_attach_node -U $PCP_USER -W $PCP_PASS -h $KB_POOL_VIP -n $IM_NODE]" >> $RECOVERY_LOG_DIR 2>&1 
        pcp_attach_node -U $PCP_USER -W $PCP_PASS -h $KB_POOL_VIP -p $KB_POOL_PCP_PORT -n $IM_NODE >> $RECOVERY_LOG_DIR 2>&1
        ksql -p $KB_POOL_PORT -U $KB_USER   -d $KB_DATANAME -h $KB_POOL_VIP -c "show pool_nodes;"  >> $RECOVERY_LOG_DIR 2>&1
        sleep 1
        echo "`date +'%Y-%m-%d %H:%M:%S'` attach end.. " >> $RECOVERY_LOG_DIR 2>&1
    else
        echo "`date +'%Y-%m-%d %H:%M:%S'` ALL NODES ARE UP STATUS!" >> $RECOVERY_LOG_DIR 2>&1
    fi
fi

#8 very important, set my flag is 0.
echo 0 > ${KB_DATA_PATH}/${KB_RECOVERY_FLAG}




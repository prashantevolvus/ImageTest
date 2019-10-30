CONTAINER=$1
INDEX=$(echo $2 | cut -d ',' -f 1)
FILE_NAME=$(echo $2 | cut -d ',' -f 2)
FILE_TYPE=$(echo $2 | cut -d ',' -f 3)
DELIMITER=$(echo $2 | cut -d ',' -f 4)
QUOTE=$(echo $2 | cut -d ',' -f 5)
HEADER=$(echo $2 | cut -d ',' -f 6)
TRIMSPOOL=$(echo $2 | cut -d ',' -f 7)
LINESIZE=$(echo $2 | cut -d ',' -f 8)
SPOOL_FILE_NAME=$(echo $2 | cut -d ',' -f 9)
CONNECTION_NAME=$(echo $2 | cut -d ',' -f 10)
if test "$PRECISION100_RUNTIME_SIMULATION_MODE" = "TRUE"; then
echo " START SPOOL ADAPTOR $FILE_NAME"
sleep $PRECISION100_RUNTIME_SIMULATION_SLEEP;
echo " END SPOOL ADAPTOR $FILE_NAME"
exit;
fi
echo " START SPOOL ADAPTOR $FILE_NAME"
source $PRECISION100_OPERATORS_FOLDER/spool/conf/.operator.env.sh
if [ -z "$DELIMITER" ]; then
DELIMITER=${DEFAULT_DELIMITER:-,}
fi
if [ -z "$QUOTE" ]; then
QUOTE=${DEFAULT_QUOTE:-OFF}
fi
if [ -z "$HEADER" ]; then
HEADER=${DEFAULT_HEADER:-OFF}
fi
EXT=$(echo "${FILE_NAME#*.}" | tr [:lower:] [:upper:])
SCRIPT_FILE="$PRECISION100_OPERATOR_SPOOL_FOLDER/$FILE_NAME.sql"
echo "SELECT * FROM $FILE_NAME;" > $SCRIPT_FILE
if [ "${FILE_NAME#*.}" == "SQL" ]; then
SCRIPT_FILE="$PRECISION100_EXECUTION_CONTAINER_FOLDER/$CONTAINER/$FILE_NAME"
fi
MARKUP="SET COLSEP $DELIMITER"
if [ "$MARKUP_CSV_SUPPORTED" == "TRUE" ]; then
MARKUP="SET MARKUP CSV ON DELIMITER $DELIMITER QUOTE $QUOTE"
fi
if [ "$DELIMITER" == "FIXED" ]; then
MARKUP="SET COLSEP ''"
fi
SPOOL_FILE="$PRECISION100_OPERATOR_SPOOL_FOLDER/${SPOOL_FILE_NAME:-$FILE_NAME.csv}"
CONNECTION_STRING=$($PRECISION100_BIN_FOLDER/get-connection-string.sh "$CONNECTION_NAME")
function spool() {
sqlplus -s /nolog <<EOF >> /dev/null
CONNECT $1
SPOOL $2
SET HEADING $3 
SET PAGESIZE 0 EMBEDDED ON
SET UNDERLINE OFF
SET FEEDBACK OFF
SET TERM OFF
SET PAGES 0
SET LINESIZE $7 
SET TRIMSPOOL $6 
SET TRIM ON
SET VERIFY OFF
$4
@$5
spool off;
EOF
}
$PRECISION100_BIN_FOLDER/audit.sh $0 "PRE-SPOOL" "$CONTAINER / $FILE_NAME" "SPOOL" $0 "START"
spool $CONNECTION_STRING "$SPOOL_FILE" "$HEADER" "$MARKUP" "$SCRIPT_FILE" "$TRIMSPOOL" "$LINESIZE"
#$PRECISION100_OPERATORS_FOLDER/spool/bin/spool.sh $FILE_NAME $DELIMITER $QUOTE $SPOOL_FILE $CONNECTION_STRING
if [ $DELIMITER != "FIXED" ]; then

	 sed -e 's/ *| */|/g' $SPOOL_FILE > temp_file_$spool_file
	mv temp_file_$spool_file $SPOOL_FILE

fi
$PRECISION100_BIN_FOLDER/audit.sh $0 "POST-SPOOL" "$CONTAINER / $FILE_NAME" "SPOOL" $0 "END"
echo " END SPOOL ADAPTOR $FILE_NAME"
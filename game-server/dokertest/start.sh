tail -f output.txt | {
    while IFS= read -r line; do
        # echo "$line"
        if [[ "$line" == *"Done"* ]]; then
            echo "startAck"
            pkill -P $$ tail  # 현재 스크립트의 부모 프로세스로 속한 tail 프로세스를 종료
            break
        fi
    done
}

parent_path=$( cd "$(dirname "${BASH_SOURCE}")" ; pwd -P )

for f in *.xi
do
    if grep -q $f "group_of_anonymous"*;
    then
        echo $f;
    fi

done

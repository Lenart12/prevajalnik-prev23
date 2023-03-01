MOODLE_USER=$1
MOODLE_PW=$2
SUBMISSION_ID=$3
ZIP_FILENAME=$4

# Verify config
set -e

[[ $MOODLE_USER ]]
[[ $MOODLE_PW ]]
[[ $SUBMISSION_ID ]]
[[ $ZIP_FILENAME ]]

#------------------------------
# Get CSRF token
wget --save-cookies cookies.txt \
     --keep-session-cookies \
     -Otmp.html \
     https://ucilnica.fri.uni-lj.si/login/index.php
LOGIN_TOKEN=$(grep -Po '"logintoken" value="\K\w+' tmp.html | head -n1)
# Login
wget --load-cookies cookies.txt \
     --save-cookies cookies.txt \
     --keep-session-cookies \
     --post-data "anchor=&logintoken=$LOGIN_TOKEN&username=$MOODLE_USER&password=$MOODLE_PW" \
     -Otmp.html \
     https://ucilnica.fri.uni-lj.si/login/index.php
SESSKEY=$(grep -Po '"sesskey" value="\K\w+' tmp.html | head -n1)
USERID=$(grep -Po 'data-userid="\K\w+' tmp.html | head -n1)

echo $LOGIN_TOKEN $SESSKEY $USERID

# Delete old submission
wget --load-cookies cookies.txt \
     --post-data "id=$SUBMISSION_ID&action=removesubmission&userid=$USERID&sesskey=$SESSKEY" \
     -O/dev/null \
     https://ucilnica.fri.uni-lj.si/mod/assign/view.php


# Get upload paramters
wget --load-cookies cookies.txt \
     -Otmp.html \
     "https://ucilnica.fri.uni-lj.si/mod/assign/view.php?id=$SUBMISSION_ID&action=editsubmission"

AUTHOR=$(grep -Po '"author":"\K[\w\s]+' tmp.html | head -n1)
ITEM_ID=$(grep -Po '"itemid":\K\w+' tmp.html | head -n1)
CLIENT_ID=$(grep -Po '"client_id":"\K\w+' tmp.html | head -n1)
CTX_ID=$(grep -Po '"context" value="\K\w+' tmp.html | head -n1)
LAST_MODIFIED=$(grep -Po '"lastmodified" type="hidden" value="\K\w+' tmp.html | head -n1)

echo $AUTHOR $ITEM_ID $CLIENT_ID $CTX_ID $LAST_MODIFIED

# Upload file
curl -b cookies.txt \
     -o /dev/null \
     -F "repo_upload_file=@$ZIP_FILENAME;type=application/x-zip-compressed" \
     -F "title=" \
     -F "author=$AUTHOR" \
     -F "license=public" \
     -F "itemid=$ITEM_ID" \
     -F "repo_id=4" \
     -F "p=" \
     -F "page=" \
     -F "env=filemanager" \
     -F "sesskey=$SESSKEY" \
     -F "client_id=$CLIENT_ID" \
     -F "itemid=$ITEM_ID" \
     -F "maxbytes=52428800" \
     -F "areamaxbytes=-1" \
     -F "ctx_id=$CTX_ID" \
     -F "savepath=/" \
     https://ucilnica.fri.uni-lj.si/repository/repository_ajax.php?action=upload


# Submit draft
curl -b cookies.txt \
     -o /dev/null \
     -d "lastmodified=$LAST_MODIFIED&id=$SUBMISSION_ID&userid=$USERID&action=savesubmission&sesskey=$SESSKEY&_qf__mod_assign_submission_form=1&submissionstatement=1&files_filemanager=$ITEM_ID&submitbutton=Shrani+spremembe" \
     https://ucilnica.fri.uni-lj.si/mod/assign/view.php

rm cookies.txt
rm tmp.html
#!/bin/bash
date + '%d/%m/%Y %H:%M:%S'
echo "Updating Git repositories..."
/usr/bin/find /local/Git -maxdepth 1 -type d -print -execdir git --git-dir={}/.git --work-tree=/local/Git/{} pull --all \;
echo "Git repositories were updated."
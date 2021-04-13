#!/bin/bash

echo "num args: $#"

if [ $# -lt 3 ]; then
    echo "usage: run_NWbandedgotoh.sh seqA.fa seqB.fa bandlength"
    exit 1
fi

seq1=$1
seq2=$2
bandlength=$3


dirname=`dirname $0`

java -cp $dirname/src NWalign $seq1 $seq2 NB $bandlength

exit $?


#!/bin/bash

set -x


for file in *.dot; do
    dot -Tpdf $file > $file.pdf
done


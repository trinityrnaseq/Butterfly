#!/bin/bash

set -x


java -jar ../Butterfly.jar -N 100000 -L 200 -F 10000 -C AACS.graph --stderr -V 15 --PasaFly --NO_EM_REDUCE --generate_intermediate_dot_files

for file in *.dot; do
    dot -Tpdf $file > $file.pdf
done


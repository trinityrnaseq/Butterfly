#!/bin/bash

set -ex

java -jar ../Butterfly.jar -N 100000 -L 200 -F 10000 -C c0.graph --stderr -V 15 --PasaFly --NO_EM_REDUCE --generate_intermediate_dot_files

for file in *.dot; do
    dot -Tpdf $file > $file.pdf
done




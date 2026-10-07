#!/usr/bin/env bash
cd src
javac *.java

# SET="Default"
MAX_VALUE=100000
OPS_PER_THREAD=1000000
WARMUP=4
MEASURE=10
for SET in LocalLog GlobalLog; do
    for DIST in Uniform Normal; do
        for Ratio in 1:1:8 1:1:0; do
            echo "=================================================="
            echo "Set: $SET Case: Dist=$DIST, Ratio=$Ratio"
            echo "=================================================="
            for T in 1 2 4 8 16 32 48 64; do
            echo "--> Threads=$T, Dist=$DIST, Ratio=$Ratio..."
            # java Main <T> <S> <D> <V> <A>:<R>:<C> <O> <W> <M>
            java Main $T $SET $DIST $MAX_VALUE $Ratio $OPS_PER_THREAD $WARMUP $MEASURE
            done
        done
    done
done
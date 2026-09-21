# Compile this plot using
# 'gnuplot pdc.p'

# Output size
set terminal png size 800,500

# Output filename
set output 'pdc.png'

# Graphics title
set title "Quicksort performance on PDC Dardel"

# Set x and y label
set xlabel 'threads'
set ylabel 'ms'

# Plot the data
# using X:Y means plot using column X and column Y
# Here column 1 is number of threads
# Column 2, 3, 4, 6 are the speedup
plot "pdc.dat" using 1:2 with linespoints title 'Sequential', \
     "pdc.dat" using 1:3 with linespoints title 'ExecutorService', \
     "pdc.dat" using 1:4 with linespoints title 'ForkJoinPool', \
     "pdc.dat" using 1:5 with linespoints title 'ParallelStream',\
     "pdc.dat" using 1:6 with linespoints title 'JavaSort'

# Compile this plot using
# 'gnuplot plot.p'

# Output size and image type
set terminal pdfcairo enhanced font "Arial,12" size 8in,5in

# Output filename
set output 'task1_2.pdf'

# Graphics title
set title "Execution Time vs Threads (LockFreeSkipList)"

# Set x and y label
set xlabel 'Number of threads'
set ylabel 'Average Execution Time (ms)'

# Set grid and legend (key) position
set grid
set key top left

# Ensure x-axis ticks match your thread values
set xtics (1, 2, 4, 8, 16, 32, 48, 64)

# Plot the data from pdc.dat
# Column 1: Threads
# Column 2: Uniform 1:1:8
# Column 3: Uniform 1:1:0
# Column 4: Normal 1:1:8
# Column 5: Normal 1:1:0
plot "pdc.dat" using 1:2 with linespoints linewidth 2 pointtype 7 title 'Uniform 1:1:8', \
     "pdc.dat" using 1:3 with linespoints linewidth 2 pointtype 5 title 'Uniform 1:1:0', \
     "pdc.dat" using 1:4 with linespoints linewidth 2 pointtype 9 title 'Normal 1:1:8', \
     "pdc.dat" using 1:5 with linespoints linewidth 2 pointtype 11 title 'Normal 1:1:0'
# DD2443/FDD3008 Lab 3 Skeleton Files

The archive contains:

- `REPORT.pdf` / `REPORT.md`: Report template in Markdown and compiled using `pandoc -o REPORT.pdf REPORT.md`.
- `Distribution.java`
    - `Distribution.Uniform(seed, min, max)`: Uniform distribution over [min, max).
    - `Distribution.Discrete(seed, prob)`: Discrete distribution over [0, prob.length), where the probability of i is prob[i]/total where total is the sum of prob.
    - `Distribution.Normal(seed, sample, min, max)`: Approximation of the normal distribution using the sum of uniform distributions [min, max). Sample is the number of random variables. This is based on the central limit theorem.
- `Experiment.java`: Small example of how an experimental setup may look like.
- `LockFreeSet.java`: Interface to a LockFreeSet. Add/Remove/Contains has threadId for assisting with the local log sampling methods.
    - `boolean add(threadId, x)`: Add x to set.
    - `boolean remove(threadId, x)`: Removes x from set.
    - `boolean contains(threadId, x)`: Check x is in set.
    - `Log.Entry[] getLog()`: Get the linearization points as a log.
    - `void reset()`: Clear the log and the set.
- `LockFreeSkipList.java`: Our default implementation of LockFreeSet (does implement the log).
- `Log.java`: Contains Log related methods and classes.
    - `Log.Entry`: Class for keeping Log information
    - `boolean validate(Log.Entry[] log)`: Class method for validating the sequential consistency of a log.
- `Main.java`: Program for testing the LockFreeSkipList.

We have provided you with a simple program for testing in `Main.java`.
The arguments of the program are the following:

```
# <T>  Number of threads to use.
# <S>  Default, Locked, LocalLog, GlobalLog version of LockFreeSkipList.
# <D>  Normal or Uniform of sampling.
# <V>  Max value to sample (samples 0-MaxValue).
# <A>:<R>:<C>  Distribution of adds, removes, and contains.
# <O>  Number of operations to execute per thread.
# <W>  Measurement rounds to warm up the JVM.
# <M>  Number of measurements for the final statistics.
java Main <T> <S> <D> <V> <A>:<R>:<C> <O> <W> <M>
```

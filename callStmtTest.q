mutable int main(int arg) {
    print1776();
    return fib(arg);
}

mutable int print1776() {
    print(1776);
    return 0;
}

int fib(int x) {
    if (x <= 1)
        return 1;
    return x * fib(x - 1);
}
close all;

figure;
linewidth = 2;
zeros = load('zeros.txt');
ones = load('ones.txt');
random = load('random.txt');
x_zeros = zeros(:,1);
x_ones = ones(:,1);
x_random = random(:,1);
y_zeros = zeros(:,2);
y_ones = ones(:,2);
y_random = random(:,2);

plot(x_zeros, y_zeros, 'Linewidth', linewidth); hold on;
plot(x_ones, y_ones, 'Linewidth', linewidth); hold on;
plot(x_random, y_random, 'Linewidth', linewidth); hold on;

legend('Empty key', '1-filled key', 'Random key');
grid on;

figure;
linewidth = 1;
zeros = load('changesZeros.txt');
ones = load('changesOnes.txt');
random = load('changesRandom.txt');
x_zeros = zeros(:,1);
x_ones = ones(:,1);
x_random = random(:,1);
y_zeros = zeros(:,2);
y_ones = ones(:,2);
y_random = random(:,2);

bar(x_zeros, [y_zeros y_ones y_random], 'grouped'); hold on;
ylim([-1 13]);
legend('Empty key', '1-filled key', 'Random key');
grid on;
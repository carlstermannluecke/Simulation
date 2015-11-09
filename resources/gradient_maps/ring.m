%creates a gradient map where the gradients converge on a ring around a center
%point
% R: not used
% G: x gradient (0.5 intensity corresponds to 0 gradient)
% B: y gradient

%square size in pixels
size = 1024;

%position of ring center
center = [512 512];
%radius of ring
radius = 300;
%how strongly the ring itself attracts vs how strong the gradient around
%the ring is
width_factor = 65;

r = zeros(size);
[coords1, coords2] = meshgrid(1:size, 1:size);
%compute direction relative to center point
x_grad_c = center(1) - coords1;
y_grad_c = center(2) - coords2;
%compute direction relative to ring
x_grad = x_grad_c + x_grad_c ./ sqrt(x_grad_c .^2 + y_grad_c .^2) * -radius;
y_grad = y_grad_c + y_grad_c ./ sqrt(x_grad_c .^2 + y_grad_c .^2) * -radius;

x_grad_norm = x_grad_c ./ sqrt(x_grad_c .^2 + y_grad_c .^2);
y_grad_norm = y_grad_c ./ sqrt(x_grad_c .^2 + y_grad_c .^2);
%add the "pull" of the ring itself
x_grad = x_grad - y_grad_norm * width_factor;
y_grad = y_grad + x_grad_norm * width_factor;

%normalize all gradient vectors to same length
magn = sqrt(x_grad .^ 2 + y_grad .^2);
x_grad = x_grad ./ magn;
y_grad = y_grad ./ magn;



%gradient visualization with arrows
gradient_pos_x = 1:30:size;
gradient_pos_x = repmat(gradient_pos_x, length(gradient_pos_x), 1);
gradient_pos_y = gradient_pos_x' * -1;
selected_gradients_x = x_grad(1:30:end, 1:30:end);
selected_gradients_y = y_grad(1:30:end, 1:30:end) * -1;
figure()
quiver(gradient_pos_x, gradient_pos_y, selected_gradients_x, selected_gradients_y);
xlim([1 size]);
ylim([-size -1]);

%normalization for encoding in RGB
total_min = min(min(x_grad(:)), min(y_grad(:)));
total_max = max(max(x_grad(:)), max(y_grad(:)));
x_grad = x_grad / (total_max - total_min)  + 0.5;
y_grad = y_grad / (total_max - total_min)  + 0.5;
figure()
img = zeros(size, size, 3);
img(:, :, 1) = r;
img(:, :, 2) = x_grad;
img(:, :, 3) = y_grad;
imshow(img);
%uncomment this to save the created image
%imwrite(img, 'ring.png');
# crawler-lazada-api

This repo crawler:
- All products on shop by API
- All comments of product by API

How to pass captcha on Lazada: 
- Go to web site get cookie and copy two value: tfstk, x5sec and replace it on main function
<img width="1348" alt="Screenshot 2024-08-14 at 20 18 42" src="https://github.com/user-attachments/assets/8c412339-0371-48fc-922e-da62b711a152">

The result:
```[
  {
    "id": "1864127769",
    "comment": "Giao hàng nhanh đóng gói cẩn thận đầy đủ số lượng đã đặt hàng của shop lơn nên yên tâm mua hàng nhận hàng ưng nha sẽ tiếp tục ủng hộ tiếp khi cần  ",
    "rating": "5",
    "reviewTime": "2 tuần trước",
    "totalReview": "11640",
    "orderCount": "77.3K sold"
  },
  {
    "id": "1864127769",
    "comment": "Nhẹ nhàng và không chứa xà phòng, Hiệu quả trong việc loại bỏ bụi bẩn và dầu, Công thức không chứa cồn,  ",
    "rating": "5",
    "reviewTime": "5 ngày trước",
    "totalReview": "11640",
    "orderCount": "77.3K sold"
  },
  {
    "id": "1864127769",
    "comment": "Nhẹ nhàng và không chứa xà phòng, Loại bỏ trang điểm hiệu quả, Đủ nhẹ cho da nhạy cảm, Làm da mềm mịn, Làm da mềm mịn, Nhẹ nhàng với da, ",
    "rating": "5",
    "reviewTime": "3 tuần trước",
    "totalReview": "11640",
    "orderCount": "77.3K sold"
  }]

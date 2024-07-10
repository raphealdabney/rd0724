import { useState, useRef, useEffect} from "react";
import logo from './logo512.jpg';
import './App.css';
import SAMPLE_DATA from "./sample";
import Axios from 'axios';

const API_BASE_URL = "http://localhost:8082"

function App() {

   const dbRef = useRef(null);
   const soundRef = useRef(null);
   const [time,setTime] = useState(null);
   const [firstTime,setFirstTime] = useState(true);
   const [activeMenu,setActiveMenu] = useState('pos');
   const [loadingSampleData,setLoadingSampleData] = useState(false);
   const [moneys,setMoneys] = useState([1, 5, 10, 20, 50, 100]);
   const [products,setProducts] = useState([]);
   const [keyword,setKeyword] = useState("");
   const [cart,setCart] = useState([]);
   const [cash,setCash] = useState(0);
   const [change,setChange] = useState(0);
   const [isShowModalReceipt,setIsShowModalReceipt] = useState(false);
   const [receiptNo,setReceiptNo] = useState(null);
   const [receiptDate,setReceiptDate] = useState(null);
   const [filteredProducts,setFilteredProducts] = useState([]);
   const [total,setTotal] = useState(0);
   const [receiptHTML, setReceiptHTML] = useState('');
   const [discountPercentage, setDiscountPercentage] = useState(0);
   const [checkoutDate, setCheckoutDate] = useState(new Date());


   useEffect(()=>{
      initDatabase();
      soundRef.current = new Audio();
      setCheckoutDate(formatDate(new Date()));
   },[]);

   useEffect(()=>{
      updateChange();
   },[checkoutDate, total, discountPercentage, cash,cart]);

   const loadDatabase = async () => {
     let databaseObject = await window.idb.openDB("tailwind_store", 1, {
       upgrade(db, oldVersion, newVersion, transaction) {
         db.createObjectStore("products", {
           keyPath: "id",
           autoIncrement: true,
         });
         db.createObjectStore("sales", {
           keyPath: "id",
           autoIncrement: true,
         });
       },
     });

     return {
       databaseObject,
       getProduct: async (product) => await dbRef.current.databaseObject.get("products",product.id),
       getProducts: async () => await dbRef.current.databaseObject.getAll("products"),
       addProduct: async (product) => {
          let existingProd = await dbRef.current.getProduct(product);
          if (existingProd) {
             return;
          }
          await dbRef.current.databaseObject.add("products", product)
       },
       editProduct: async (product) =>
         await dbRef.current.databaseObject.put("products", product.id, product),
       deleteProduct: async (product) => await dbRef.current.databaseObject.delete("products", product.id),
     };
   }

   const formatDate = (date)=>{
    let y = date.getFullYear();
    let m = date.getMonth()+1;
    let d = date.getDate();
     return y + "-" + (m < 10 ? '0'+ m : m)  + "-" + (d < 10 ? '0'+ d : d)
   }

   const initDatabase = async () => {
     dbRef.current = await loadDatabase();
     await loadProducts();
  };
   const loadProducts = async() => {
     let {data: freshProds} = await Axios.get(`${API_BASE_URL}/products`);
     setProducts(freshProds);
     setFilteredProducts([...freshProds]);
     console.log("products loaded", freshProds);
  };
   const startWithSampleData = async() => {
     const data = SAMPLE_DATA;
     let products = data.products;
     for (let product of data.products) {
        await dbRef.current.addProduct(product);
     }
     setDefaultFirstTime(false);

  };
   const startBlank = () => {
     setDefaultFirstTime(false);
  };
   const setDefaultFirstTime = (ft) => {
     if (ft) {
      localStorage.removeItem("first_time");
     } else {
      localStorage.setItem("first_time", new Date().getTime());
     }
     setFirstTime(ft);
  };
   const filterProducts = () => {
     const rg = keyword ? new RegExp(keyword, "gi") : null;
     setFilteredProducts(products.filter((p) => !rg || p.name.match(rg)));
  };
   const addToCart = (product) => {
      let tempCart = [...cart];
     const index = findCartIndex(product);
     if (index === -1) {
      tempCart.push({
        productId: product.id,
        image: product.image,
        name: product.name,
        price: product.price,
        option: product.option,
        qty: 1,
      });
     } else {
      tempCart[index].qty += 1;
     }
     setCart(tempCart);
     beep();
     updateChange();
  };
   const findCartIndex = (product) => {
     return cart.findIndex((p) => p.productId === product.id);
  };
   const addQty = (item, qty) => {
     const index = cart.findIndex((i) => i.productId === item.productId);
     if (index === -1) {
      return;
     }
     const afterAdd = item.qty + qty;
     let tempCart = [...cart];
     if (afterAdd === 0) {
      tempCart.splice(index, 1);
      clearSound();
     } else {
      tempCart[index].qty = afterAdd;
      beep();
     }
     setCart(tempCart);
     updateChange();
  };
  const updateQuantity = (item,qty) => {
     const index = cart.findIndex((i) => i.productId === item.productId);
     if (index === -1) {
     return;
     }

     let cartCopy = [...cart];
     cartCopy[index].qty = qty;
     setCart(cartCopy);
     updateChange();
 }

   const addCash = (amount) => {
     setCash((cash || 0) + amount);
     updateChange(false);
     beep();
  };
   const getItemsCount = () => {
     return cart.reduce((count, item) => count + item.qty, 0);
  };
   const updateChange = async (updateTotal=true)=> {
      let subtotal = updateTotal ? await calculateTotal(cart) : total;
      
     setChange(cash - subtotal);
  };
   const updateCash = (value) => {
     setCash(parseFloat(value.replace(/[^0-9]+/g, "")));
     updateChange();
  };
   const getTotalPrice = ()=> {
     return cart.reduce(
      (total, item) => total + item.qty * item.price,
      0
     );
  };
   const submitable = () => {
     return change >= 0 && cart.length > 0;
  };
   const submit = () => {
     const time = new Date();
     setIsShowModalReceipt(true);
     setReceiptNo(`TR-SHOP-${Math.round(time.getTime() / 1000)}`);
     setReceiptDate(dateFormat(time));
     Axios.post(`${API_BASE_URL}/orders`, formatCartRequest())
     .then(({data:resp})=>{
        setReceiptHTML(resp.html);
     })
     .catch(e=>{
        setReceiptHTML(`<div class="text-center">
          <img src="assets/img/receipt-logo.png" alt="Tailwind POS" class="mb-3 w-8 h-8 inline-block">
          <h2 class="text-xl font-semibold">TAILWIND POS</h2>
          <p>CABANG KONOHA SELATAN</p>
        </div>
        <div class="flex mt-4 text-xs">
          <div class="flex-grow">No: <span x-text="receiptNo"></span></div>
          <div x-text="receiptDate"></div>
        </div>
        <hr class="my-2">
        <div>
          <table class="w-full text-xs">
            <thead>
             <tr>
                <th class="py-1 w-1/12 text-center">#</th>
                <th class="py-1 text-left">Item</th>
                <th class="py-1 w-2/12 text-center">Qty</th>
                <th class="py-1 w-3/12 text-right">Subtotal</th>
             </tr>
            </thead>
            <tbody>
             <template x-for="(item, index) in cart" :key="item">
                <tr>
                  <td class="py-2 text-center" x-text="index+1"></td>
                  <td class="py-2 text-left">
                    <span x-text="item.name"></span>
                    <br/>
                    <small x-text="priceFormat(item.price)"></small>
                  </td>
                  <td class="py-2 text-center" x-text="item.qty"></td>
                  <td class="py-2 text-right" x-text="priceFormat(item.qty * item.price)"></td>
                </tr>
             </template>
            </tbody>
          </table>
        </div>
        <hr class="my-2">
        <div>
          <div class="flex font-semibold">
            <div class="flex-grow">TOTAL</div>
            <div x-text="priceFormat(getTotalPrice())"></div>
          </div>
          <div class="flex text-xs font-semibold">
            <div class="flex-grow">PAY AMOUNT</div>
            <div x-text="priceFormat(cash)"></div>
          </div>
          <hr class="my-2">
          <div class="flex text-xs font-semibold">
            <div class="flex-grow">CHANGE</div>
            <div x-text="priceFormat(change)"></div>
          </div>
        </div>`);
     });
  };
   const closeModalReceipt = ()=> {
     setIsShowModalReceipt(false);
  };
   const dateFormat = (date) => {
     const formatter = new Intl.DateTimeFormat('id', { dateStyle: 'short', timeStyle: 'short'});
     return formatter.format(date);
  };
   const numberFormat = (number)=> {
    return number;
     return (number || "")
      .toString()
      .replace(/^0|\./g, "")
      .replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1.");
   };
   const priceFormat = (number) => {
     return number ? `USD. ${Math.round(number*100)/100}` : `USD. 0`;
  };
   const clear = () => {
      setCart([]);
      setTotal(0);
     setCash(0);
     setChange(0);
     setReceiptNo(null);
     setReceiptDate(null);
     clearSound();
  };
   const beep = () => {
     playSound("assets/sound/beep-29.mp3");
  };
   const clearSound = () =>{
     playSound("assets/sound/button-21.mp3");
  };
   const playSound = (src)=> {
     soundRef.current.src = src;
     soundRef.current.play();
  };
   const printAndProceed = () => {
     const receiptContent = document.getElementById('receipt-content');
     const titleBefore = document.title;
     const printArea = document.getElementById('print-area');

     printArea.innerHTML = receiptContent.innerHTML;
     document.title = receiptNo;

     window.print();
     setIsShowModalReceipt(false)

     printArea.innerHTML = '';
     document.title = titleBefore;

     // TODO save sale data to database

     if (window.confirm("Do you want to reset the order?")) clear();
   }

   const formatCartRequest = () => {
    return {
      discountPercent: discountPercentage,
      checkoutDate,
      items: cart.map(itm=>{
        let product = products.find(p=>p.id == itm.productId);
        return {
          rentalDays:itm.qty,
          toolCode:product.tool_code
        }
      })
    };
   }

   const calculateTotal =  async (cart) => {
      return new Promise((resolve,reject)=>{
      
        let cartRequest = formatCartRequest();
         Axios.post(`${API_BASE_URL}/orders/calc-cart-price`, cartRequest)
         .then(resp=>{
            setTotal(resp.data);
            resolve(resp.data);
         })
         .catch(e=>{
            setTotal(99999.31);
            resolve(99999.31);
            // reject(e);
            console.error("cannot calculate cost.")
         })
      })
   }

  return (
    <main>
    <div className="hide-print flex flex-row h-screen antialiased text-blue-gray-800">
     { /* left sidebar */ }
     <div className="flex flex-row w-auto flex-shrink-0 pl-4 pr-2 py-4">
        <div className="flex flex-col items-center py-4 flex-shrink-0 w-20 bg-cyan-500 rounded-3xl">
          <a href="#"
             className="flex items-center justify-center h-12 w-12 bg-cyan-50 text-cyan-700 rounded-full">
            <svg xmlns="http://www.w3.org/2000/svg" width="123.3" height="123.233" viewBox="0 0 32.623 32.605">
             <path d="M15.612 0c-.36.003-.705.01-1.03.021C8.657.223 5.742 1.123 3.4 3.472.714 6.166-.145 9.758.019 17.607c.137 6.52.965 9.271 3.542 11.768 1.31 1.269 2.658 2 4.73 2.57.846.232 2.73.547 3.56.596.36.021 2.336.048 4.392.06 3.162.018 4.031-.016 5.63-.221 3.915-.504 6.43-1.778 8.234-4.173 1.806-2.396 2.514-5.731 2.516-11.846.001-4.407-.42-7.59-1.278-9.643-1.463-3.501-4.183-5.53-8.394-6.258-1.634-.283-4.823-.475-7.339-.46z" fill="#fff"/><path d="M16.202 13.758c-.056 0-.11 0-.16.003-.926.031-1.38.172-1.747.538-.42.421-.553.982-.528 2.208.022 1.018.151 1.447.553 1.837.205.198.415.313.739.402.132.036.426.085.556.093.056.003.365.007.686.009.494.003.63-.002.879-.035.611-.078 1.004-.277 1.286-.651.282-.374.392-.895.393-1.85 0-.688-.066-1.185-.2-1.506-.228-.547-.653-.864-1.31-.977a7.91 7.91 0 00-1.147-.072zM16.22 19.926c-.056 0-.11 0-.16.003-.925.031-1.38.172-1.746.539-.42.42-.554.981-.528 2.207.02 1.018.15 1.448.553 1.838.204.198.415.312.738.4.132.037.426.086.556.094.056.003.365.007.686.009.494.003.63-.002.88-.034.61-.08 1.003-.278 1.285-.652.282-.374.393-.895.393-1.85 0-.688-.066-1.185-.2-1.506-.228-.547-.653-.863-1.31-.977a7.91 7.91 0 00-1.146-.072zM22.468 13.736c-.056 0-.11.001-.161.003-.925.032-1.38.172-1.746.54-.42.42-.554.98-.528 2.207.021 1.018.15 1.447.553 1.837.205.198.415.313.739.401.132.037.426.086.556.094.056.003.364.007.685.009.494.003.63-.002.88-.035.611-.078 1.004-.277 1.285-.651.282-.375.393-.895.393-1.85 0-.688-.065-1.185-.2-1.506-.228-.547-.653-.864-1.31-.977a7.91 7.91 0 00-1.146-.072z" fill="#00dace"/><path d="M9.937 13.736c-.056 0-.11.001-.161.003-.925.032-1.38.172-1.746.54-.42.42-.554.98-.528 2.207.021 1.018.15 1.447.553 1.837.204.198.415.313.738.401.133.037.427.086.556.094.056.003.365.007.686.009.494.003.63-.002.88-.035.61-.078 1.003-.277 1.285-.651.282-.375.393-.895.393-1.85 0-.688-.066-1.185-.2-1.506-.228-.547-.653-.864-1.31-.977a7.91 7.91 0 00-1.146-.072zM16.202 7.59c-.056 0-.11 0-.16.002-.926.032-1.38.172-1.747.54-.42.42-.553.98-.528 2.206.022 1.019.151 1.448.553 1.838.205.198.415.312.739.401.132.037.426.086.556.093.056.003.365.007.686.01.494.002.63-.003.879-.035.611-.079 1.004-.278 1.286-.652.282-.374.392-.895.393-1.85 0-.688-.066-1.185-.2-1.505-.228-.547-.653-.864-1.31-.978a7.91 7.91 0 00-1.147-.071z" fill="#00bcd4"/><g><path d="M15.612 0c-.36.003-.705.01-1.03.021C8.657.223 5.742 1.123 3.4 3.472.714 6.166-.145 9.758.019 17.607c.137 6.52.965 9.271 3.542 11.768 1.31 1.269 2.658 2 4.73 2.57.846.232 2.73.547 3.56.596.36.021 2.336.048 4.392.06 3.162.018 4.031-.016 5.63-.221 3.915-.504 6.43-1.778 8.234-4.173 1.806-2.396 2.514-5.731 2.516-11.846.001-4.407-.42-7.59-1.278-9.643-1.463-3.501-4.183-5.53-8.394-6.258-1.634-.283-4.823-.475-7.339-.46z" fill="#fff"/><path d="M16.202 13.758c-.056 0-.11 0-.16.003-.926.031-1.38.172-1.747.538-.42.421-.553.982-.528 2.208.022 1.018.151 1.447.553 1.837.205.198.415.313.739.402.132.036.426.085.556.093.056.003.365.007.686.009.494.003.63-.002.879-.035.611-.078 1.004-.277 1.286-.651.282-.374.392-.895.393-1.85 0-.688-.066-1.185-.2-1.506-.228-.547-.653-.864-1.31-.977a7.91 7.91 0 00-1.147-.072zM16.22 19.926c-.056 0-.11 0-.16.003-.925.031-1.38.172-1.746.539-.42.42-.554.981-.528 2.207.02 1.018.15 1.448.553 1.838.204.198.415.312.738.4.132.037.426.086.556.094.056.003.365.007.686.009.494.003.63-.002.88-.034.61-.08 1.003-.278 1.285-.652.282-.374.393-.895.393-1.85 0-.688-.066-1.185-.2-1.506-.228-.547-.653-.863-1.31-.977a7.91 7.91 0 00-1.146-.072zM22.468 13.736c-.056 0-.11.001-.161.003-.925.032-1.38.172-1.746.54-.42.42-.554.98-.528 2.207.021 1.018.15 1.447.553 1.837.205.198.415.313.739.401.132.037.426.086.556.094.056.003.364.007.685.009.494.003.63-.002.88-.035.611-.078 1.004-.277 1.285-.651.282-.375.393-.895.393-1.85 0-.688-.065-1.185-.2-1.506-.228-.547-.653-.864-1.31-.977a7.91 7.91 0 00-1.146-.072z" fill="#00dace"/><path d="M9.937 13.736c-.056 0-.11.001-.161.003-.925.032-1.38.172-1.746.54-.42.42-.554.98-.528 2.207.021 1.018.15 1.447.553 1.837.204.198.415.313.738.401.133.037.427.086.556.094.056.003.365.007.686.009.494.003.63-.002.88-.035.61-.078 1.003-.277 1.285-.651.282-.375.393-.895.393-1.85 0-.688-.066-1.185-.2-1.506-.228-.547-.653-.864-1.31-.977a7.91 7.91 0 00-1.146-.072zM16.202 7.59c-.056 0-.11 0-.16.002-.926.032-1.38.172-1.747.54-.42.42-.553.98-.528 2.206.022 1.019.151 1.448.553 1.838.205.198.415.312.739.401.132.037.426.086.556.093.056.003.365.007.686.01.494.002.63-.003.879-.035.611-.079 1.004-.278 1.286-.652.282-.374.392-.895.393-1.85 0-.688-.066-1.185-.2-1.505-.228-.547-.653-.864-1.31-.978a7.91 7.91 0 00-1.147-.071z" fill="#00bcd4"/></g>
            </svg>
          </a>
          <ul className="flex flex-col space-y-2 mt-12 hidden">
            <li>
             <a href="#"
                 className="flex items-center">
                <span
                  className="flex items-center justify-center h-12 w-12 rounded-2xl"
                  className="{
                    'hover:bg-cyan-400 text-cyan-100': activeMenu !== 'pos',
                    'bg-cyan-300 shadow-lg text-white': activeMenu === 'pos',
                  }"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
                  </svg>
                </span>
             </a>
            </li>
            <li>
             <a href="#"
                 className="flex items-center">
                <span className="flex items-center justify-center text-cyan-100 hover:bg-cyan-400 h-12 w-12 rounded-2xl">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
                  </svg>
                </span>
             </a>
            </li>
            <li>
             <a href="#"
                 className="flex items-center">
                <span className="flex items-center justify-center text-cyan-100 hover:bg-cyan-400 h-12 w-12 rounded-2xl">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
                  </svg>
                </span>
             </a>
            </li>
            <li>
             <a href="#"
                 className="flex items-center">
                <span className="flex items-center justify-center text-cyan-100 hover:bg-cyan-400 h-12 w-12 rounded-2xl">
                  <svg className="w-6 h-6"
                       fill="none"
                       stroke="currentColor"
                       viewBox="0 0 24 24"
                       xmlns="http://www.w3.org/2000/svg">
                    <path strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth="2"
                          d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path>
                    <path strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth="2"
                          d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                  </svg>
                </span>
             </a>
            </li>
          </ul>

        </div>
     </div>

     { /* page content */ }
     <div className="flex-grow flex">
        { /* store menu */ }
        <div className="flex flex-col bg-blue-gray-50 h-full w-full py-4">
          <div className="flex px-2 flex-row relative">
            <div className="absolute left-5 top-3 px-2 py-2 rounded-full bg-cyan-500 text-white">
             <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
             </svg>
            </div>
            <input
             type="text"
             className="bg-white rounded-3xl shadow text-lg full w-full h-16 py-4 pl-16 transition-shadow focus:shadow-2xl focus:outline-none"
             placeholder="Search here ..."
             x-model="keyword"
            />
          </div>
          <div className="h-full overflow-hidden mt-4">
            <div className="h-full overflow-y-auto px-2">

            { products.length === 0 && (
             <div
                className="select-none bg-blue-gray-100 rounded-3xl flex flex-wrap content-center justify-center h-full opacity-25"
             >
                <div className="w-full text-center">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-24 w-24 inline-block" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 7v10c0 2.21 3.582 4 8 4s8-1.79 8-4V7M4 7c0 2.21 3.582 4 8 4s8-1.79 8-4M4 7c0-2.21 3.582-4 8-4s8 1.79 8 4m0 5c0 2.21-3.582 4-8 4s-8-1.79-8-4" />
                  </svg>
                  <p className="text-xl">
                    YOU DON'T HAVE
                    <br/>
                    ANY PRODUCTS TO SHOW
                  </p>
                </div>
             </div>
             )}

             { filteredProducts.length === 0 && keyword.length > 0 & (
                <div
                   className="select-none bg-blue-gray-100 rounded-3xl flex flex-wrap content-center justify-center h-full opacity-25"
                >
                   <div className="w-full text-center">
                     <svg xmlns="http://www.w3.org/2000/svg" className="h-24 w-24 inline-block" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                       <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                     </svg>
                     <p className="text-xl">
                       EMPTY SEARCH RESULT
                       <br/>
                       "<span x-text="keyword" className="font-semibold"></span>"
                     </p>
                   </div>
                </div>
             )}

            { filteredProducts.length && (
             <div className="grid grid-cols-4 gap-4 pb-3">
               {filteredProducts.map(product=> {

                     return (
                        <div
                          role="button"
                          className="select-none cursor-pointer transition-shadow overflow-hidden rounded-2xl bg-white shadow hover:shadow-lg"
                          title={product.name}
                          onClick={()=>addToCart(product)}
                        >
                          <img src={'/assets/img/'+ product.image} alt={product.name} />
                          <div className="flex pb-3 px-3 text-sm -mt-3">
                            <p className="flex-grow truncate mr-1">{product.name}</p>
                            <p className="nowrap font-semibold" >{priceFormat(product.charges_daily)}</p>
                          </div>
                        </div>
                     )
               })}
             </div>
          )}
            </div>
          </div>
        </div>
        { /* end of store menu */ }

        { /* right sidebar */ }
        <div className="w-5/12 flex flex-col bg-blue-gray-50 h-full bg-white pr-4 pl-2 py-4">
          <div className="bg-white rounded-3xl flex flex-col h-full shadow">
            { /* empty cart */ }
            { cart.length  == 0 && (
               <div className="flex-1 w-full p-4 opacity-25 select-none flex flex-col flex-wrap content-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-16 inline-block" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                   <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                <p>
                   CART EMPTY
                </p>
               </div>
            )}

            { /* cart items */ }
            { cart.length > 0 && (
               <div show="cart.length > 0" className="flex-1 flex flex-col overflow-auto">
                <div className="h-16 text-center flex justify-center">
                   <div className="pl-8 text-left text-lg py-4 relative">
                     { /* cart icon */ }
                     <svg xmlns="http://www.w3.org/2000/svg" className="h-6 inline-block" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                       <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                     </svg>
                     { getItemsCount() > 0 && (
                           <div className="text-center absolute bg-cyan-500 text-white w-5 h-5 text-xs p-0 leading-5 rounded-full -right-2 top-3">{getItemsCount()}</div>
                     )}

                   </div>
                   <div className="flex-grow px-8 text-right text-lg py-4 relative">
                     { /* trash button */ }
                     <button onClick={()=>clear()} className="text-blue-gray-300 hover:text-pink-500 focus:outline-none">
                       <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 inline-block" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                         <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                       </svg>
                     </button>
                   </div>
                </div>

                <div className="flex-1 w-full px-4 overflow-auto">
                {
                   cart.map(item=>{
                      return (
                         <div className="select-none mb-3 bg-blue-gray-50 rounded-lg w-full text-blue-gray-700 py-2 px-2 flex justify-center" key={item.productId}>
                          <img src={'assets/img/' + item.image} alt="" className="rounded-lg h-10 w-10 bg-white shadow mr-2" />
                          <div className="flex-grow">
                            <h5 className="text-sm">{item.name}</h5>
                            <p className="text-xs block" > {priceFormat(item.price)}</p>
                          </div>
                          <div className="py-1">
                            <div className="w-28 grid grid-cols-4 gap-2 ml-2">
                             <button onClick={()=>updateQuantity(item, item.qty-1)} className="rounded-lg text-center py-1 text-white bg-blue-gray-600 hover:bg-blue-gray-700 focus:outline-none">
                                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-3 inline-block" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M20 12H4" />
                                </svg>
                             </button>
                             <input onChange={(e)=>updateQuantity(item, e.target.value)} value={item.qty} type="text" className="bg-white rounded-lg text-center shadow focus:outline-none focus:shadow-lg text-sm" />
                             <span style={{'display': 'flex', 'alignItems': 'center'}} className=" text-center text-sm"> days</span>
                             <button onClick={()=>updateQuantity(item, item.qty+1)} className="rounded-lg text-center py-1 text-white bg-blue-gray-600 hover:bg-blue-gray-700 focus:outline-none">
                                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-3 inline-block" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                                </svg>
                             </button>

                            </div>
                          </div>
                        </div>
                      )
                   })
                }
                </div>
               </div>
            )}
            { /* end of cart items */ }


            { /* payment info */ }
            <div className="select-none h-auto w-full text-center pt-3 pb-4 px-4">
             <div className="flex mb-3 text-lg font-semibold text-blue-gray-700">
                <div>TOTAL</div>
                <div className="text-right w-full">{priceFormat(total)}</div>
             </div>
             
             
             <div className="mb-3 text-blue-gray-700 px-3 pt-2 pb-3 rounded-lg bg-blue-gray-50">
                <div className="flex text-lg font-semibold">
                  <div className="flex-grow text-left">Discount</div>
                  <div className="flex text-right">                  
                    <input value={discountPercentage} onChange={($event)=>setDiscountPercentage($event.target.value)} type="text" className="w-28 text-right bg-white shadow rounded-lg focus:bg-white focus:shadow-lg px-2 focus:outline-none" />
                    <div className="mr-2">%</div>
                  </div>
                </div>
                <div className="flex text-lg font-semibold">
                  <div className="flex-grow text-left">Checkout Date</div>
                  <div className="flex text-right">
                    <input value={checkoutDate} onChange={($event)=>setCheckoutDate($event.target.value)} type="date" className="w-28 text-right bg-white shadow rounded-lg focus:bg-white focus:shadow-lg px-2 focus:outline-none" />
                  </div>
                </div>
                <div className="flex text-lg font-semibold">
                  <div className="flex-grow text-left">CASH</div>
                  <div className="flex text-right">
                    <div className="mr-2">USD</div>
                    <input value={numberFormat(cash)} onChange={($event)=>updateCash($event.target.value)} type="text" className="w-28 text-right bg-white shadow rounded-lg focus:bg-white focus:shadow-lg px-2 focus:outline-none" />
                  </div>
                </div>
                <hr className="my-2" />
                <div className="grid grid-cols-3 gap-2 mt-2">
                  {moneys.map(money=>{

                     return (
                        <button onClick={(e)=>addCash(money)} className="bg-white rounded-lg shadow hover:shadow-lg focus:outline-none inline-block px-2 py-1 text-sm">+<span>{numberFormat(money)}</span></button>
                     )
                  })}

                </div>
             </div>
             { change > 0 && (
                <div
                   className="flex mb-3 text-lg font-semibold bg-cyan-50 text-blue-gray-700 rounded-lg py-2 px-3"
                >
                   <div className="text-cyan-800">CHANGE</div>
                   <div
                     className="text-right flex-grow text-cyan-600">
                     {priceFormat(change)}
                   </div>
                </div>
             )}
                { change < 0.01 && (
                   <div
                      className="flex mb-3 text-lg font-semibold bg-pink-100 text-blue-gray-700 rounded-lg py-2 px-3">
                      <div
                       className="text-right flex-grow text-pink-600">
                       {priceFormat(change)}
                      </div>
                   </div>
                )}

                { change == 0 && cart.length > 0 && (
                   <div
                      className="flex justify-center mb-3 text-lg font-semibold bg-cyan-50 text-cyan-700 rounded-lg py-2 px-3"
                   >
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 inline-block" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M14 10h4.764a2 2 0 011.789 2.894l-3.5 7A2 2 0 0115.263 21h-4.017c-.163 0-.326-.02-.485-.06L7 20m7-10V5a2 2 0 00-2-2h-.095c-.5 0-.905.405-.905.905 0 .714-.211 1.412-.608 2.006L7 11v9m7-10h-2M7 20H5a2 2 0 01-2-2v-6a2 2 0 012-2h2.5" />
                      </svg>
                   </div>
                )}
                <button
                   className="text-white rounded-2xl text-lg w-full py-3 focus:outline-none"
                   className={{
                     'bg-cyan-500 hover:bg-cyan-600': submitable(),
                     'bg-blue-gray-200': !submitable()
                  }}
                   disabled={change < 0}
                   onClick={()=>submit()}
                >
                   COMPLETE ORDER
                </button>
               </div>

            { /* end of payment info */ }
          </div>
        </div>
        { /* end of right sidebar */ }
     </div>

     { /* modal first time */ }
     { firstTime && (
        <div className="fixed glass w-full h-screen left-0 top-0 z-10 flex flex-wrap justify-center content-center p-24">
          <div className="w-96 rounded-3xl p-8 bg-white shadow-xl">
            <div className="text-center">
              <svg xmlns="http://www.w3.org/2000/svg" className="inline-block" width="123.3" height="123.233" viewBox="0 0 32.623 32.605">
               <path d="M15.612 0c-.36.003-.705.01-1.03.021C8.657.223 5.742 1.123 3.4 3.472.714 6.166-.145 9.758.019 17.607c.137 6.52.965 9.271 3.542 11.768 1.31 1.269 2.658 2 4.73 2.57.846.232 2.73.547 3.56.596.36.021 2.336.048 4.392.06 3.162.018 4.031-.016 5.63-.221 3.915-.504 6.43-1.778 8.234-4.173 1.806-2.396 2.514-5.731 2.516-11.846.001-4.407-.42-7.59-1.278-9.643-1.463-3.501-4.183-5.53-8.394-6.258-1.634-.283-4.823-.475-7.339-.46z" fill="#fff"/><path d="M16.202 13.758c-.056 0-.11 0-.16.003-.926.031-1.38.172-1.747.538-.42.421-.553.982-.528 2.208.022 1.018.151 1.447.553 1.837.205.198.415.313.739.402.132.036.426.085.556.093.056.003.365.007.686.009.494.003.63-.002.879-.035.611-.078 1.004-.277 1.286-.651.282-.374.392-.895.393-1.85 0-.688-.066-1.185-.2-1.506-.228-.547-.653-.864-1.31-.977a7.91 7.91 0 00-1.147-.072zM16.22 19.926c-.056 0-.11 0-.16.003-.925.031-1.38.172-1.746.539-.42.42-.554.981-.528 2.207.02 1.018.15 1.448.553 1.838.204.198.415.312.738.4.132.037.426.086.556.094.056.003.365.007.686.009.494.003.63-.002.88-.034.61-.08 1.003-.278 1.285-.652.282-.374.393-.895.393-1.85 0-.688-.066-1.185-.2-1.506-.228-.547-.653-.863-1.31-.977a7.91 7.91 0 00-1.146-.072zM22.468 13.736c-.056 0-.11.001-.161.003-.925.032-1.38.172-1.746.54-.42.42-.554.98-.528 2.207.021 1.018.15 1.447.553 1.837.205.198.415.313.739.401.132.037.426.086.556.094.056.003.364.007.685.009.494.003.63-.002.88-.035.611-.078 1.004-.277 1.285-.651.282-.375.393-.895.393-1.85 0-.688-.065-1.185-.2-1.506-.228-.547-.653-.864-1.31-.977a7.91 7.91 0 00-1.146-.072z" fill="#00dace"/><path d="M9.937 13.736c-.056 0-.11.001-.161.003-.925.032-1.38.172-1.746.54-.42.42-.554.98-.528 2.207.021 1.018.15 1.447.553 1.837.204.198.415.313.738.401.133.037.427.086.556.094.056.003.365.007.686.009.494.003.63-.002.88-.035.61-.078 1.003-.277 1.285-.651.282-.375.393-.895.393-1.85 0-.688-.066-1.185-.2-1.506-.228-.547-.653-.864-1.31-.977a7.91 7.91 0 00-1.146-.072zM16.202 7.59c-.056 0-.11 0-.16.002-.926.032-1.38.172-1.747.54-.42.42-.553.98-.528 2.206.022 1.019.151 1.448.553 1.838.205.198.415.312.739.401.132.037.426.086.556.093.056.003.365.007.686.01.494.002.63-.003.879-.035.611-.079 1.004-.278 1.286-.652.282-.374.392-.895.393-1.85 0-.688-.066-1.185-.2-1.505-.228-.547-.653-.864-1.31-.978a7.91 7.91 0 00-1.147-.071z" fill="#00bcd4"/><g><path d="M15.612 0c-.36.003-.705.01-1.03.021C8.657.223 5.742 1.123 3.4 3.472.714 6.166-.145 9.758.019 17.607c.137 6.52.965 9.271 3.542 11.768 1.31 1.269 2.658 2 4.73 2.57.846.232 2.73.547 3.56.596.36.021 2.336.048 4.392.06 3.162.018 4.031-.016 5.63-.221 3.915-.504 6.43-1.778 8.234-4.173 1.806-2.396 2.514-5.731 2.516-11.846.001-4.407-.42-7.59-1.278-9.643-1.463-3.501-4.183-5.53-8.394-6.258-1.634-.283-4.823-.475-7.339-.46z" fill="#fff"/><path d="M16.202 13.758c-.056 0-.11 0-.16.003-.926.031-1.38.172-1.747.538-.42.421-.553.982-.528 2.208.022 1.018.151 1.447.553 1.837.205.198.415.313.739.402.132.036.426.085.556.093.056.003.365.007.686.009.494.003.63-.002.879-.035.611-.078 1.004-.277 1.286-.651.282-.374.392-.895.393-1.85 0-.688-.066-1.185-.2-1.506-.228-.547-.653-.864-1.31-.977a7.91 7.91 0 00-1.147-.072zM16.22 19.926c-.056 0-.11 0-.16.003-.925.031-1.38.172-1.746.539-.42.42-.554.981-.528 2.207.02 1.018.15 1.448.553 1.838.204.198.415.312.738.4.132.037.426.086.556.094.056.003.365.007.686.009.494.003.63-.002.88-.034.61-.08 1.003-.278 1.285-.652.282-.374.393-.895.393-1.85 0-.688-.066-1.185-.2-1.506-.228-.547-.653-.863-1.31-.977a7.91 7.91 0 00-1.146-.072zM22.468 13.736c-.056 0-.11.001-.161.003-.925.032-1.38.172-1.746.54-.42.42-.554.98-.528 2.207.021 1.018.15 1.447.553 1.837.205.198.415.313.739.401.132.037.426.086.556.094.056.003.364.007.685.009.494.003.63-.002.88-.035.611-.078 1.004-.277 1.285-.651.282-.375.393-.895.393-1.85 0-.688-.065-1.185-.2-1.506-.228-.547-.653-.864-1.31-.977a7.91 7.91 0 00-1.146-.072z" fill="#00dace"/><path d="M9.937 13.736c-.056 0-.11.001-.161.003-.925.032-1.38.172-1.746.54-.42.42-.554.98-.528 2.207.021 1.018.15 1.447.553 1.837.204.198.415.313.738.401.133.037.427.086.556.094.056.003.365.007.686.009.494.003.63-.002.88-.035.61-.078 1.003-.277 1.285-.651.282-.375.393-.895.393-1.85 0-.688-.066-1.185-.2-1.506-.228-.547-.653-.864-1.31-.977a7.91 7.91 0 00-1.146-.072zM16.202 7.59c-.056 0-.11 0-.16.002-.926.032-1.38.172-1.747.54-.42.42-.553.98-.528 2.206.022 1.019.151 1.448.553 1.838.205.198.415.312.739.401.132.037.426.086.556.093.056.003.365.007.686.01.494.002.63-.003.879-.035.611-.079 1.004-.278 1.286-.652.282-.374.392-.895.393-1.85 0-.688-.066-1.185-.2-1.505-.228-.547-.653-.864-1.31-.978a7.91 7.91 0 00-1.147-.071z" fill="#00bcd4"/></g>
              </svg>
              <h3 className="text-center text-2xl mb-8">FIRST TIME?</h3>
            </div>
            <div className="text-left">
              <button onClick={()=>startWithSampleData()} className="text-left w-full mb-3 rounded-xl bg-blue-gray-500 text-white focus:outline-none hover:bg-cyan-400 px-4 py-4">
               <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 inline-block -mt-1 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 4H6a2 2 0 00-2 2v12a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-2m-4-1v8m0 0l3-3m-3 3L9 8m-5 5h2.586a1 1 0 01.707.293l2.414 2.414a1 1 0 00.707.293h3.172a1 1 0 00.707-.293l2.414-2.414a1 1 0 01.707-.293H20" />
               </svg>
               LOAD SAMPLE DATA
              </button>
              <button onClick={()=>startBlank()} className="text-left w-full rounded-xl bg-blue-gray-500 text-white focus:outline-none hover:bg-teal-400 px-4 py-4">
               <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 inline-block -mt-1 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
               </svg>
               LEAVE IT EMPTY
              </button>
            </div>
          </div>
       </div>
     )}


     { /* modal receipt */ }
     {isShowModalReceipt && (
        <div
          className="fixed w-full h-screen left-0 top-0 z-10 flex flex-wrap justify-center content-center p-24"
       >
        {isShowModalReceipt && (
           <div
             className="fixed glass w-full h-screen left-0 top-0 z-0" onClick={()=>closeModalReceipt()}

           >
           { /* x-transition:enter="transition ease-out duration-100"
           x-transition:enter-start="opacity-0"
           x-transition:enter-end="opacity-100"
           x-transition:leave="transition ease-in duration-100"
           x-transition:leave-start="opacity-100"
           x-transition:leave-end="opacity-0" */}</div>
        )}

          {isShowModalReceipt && (

             <div
               className="w-96 rounded-3xl bg-white shadow-xl overflow-hidden z-10"
             >
             { /*x-transition:enter="transition ease-out duration-100"
             x-transition:enter-start="opacity-0 transform scale-90"
             x-transition:enter-end="opacity-100 transform scale-100"
             x-transition:leave="transition ease-in duration-100"
             x-transition:leave-start="opacity-100 transform scale-100"
             x-transition:leave-end="opacity-0 transform scale-90"*/ }
               <div id="receipt-content" className="text-left w-full text-sm p-6 overflow-auto" dangerouslySetInnerHTML={{__html:receiptHTML}}>
               </div>
               <div className="p-4 w-full">
                 <button className="bg-cyan-500 text-white text-lg px-4 py-3 rounded-2xl w-full focus:outline-none" onClick={()=>printAndProceed()}>PRINT</button>
               </div>
             </div>

          )}
          </div>

     )}
     </div>

    </main>
  );
}

export default App;

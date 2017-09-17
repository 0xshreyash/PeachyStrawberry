package com.comp30022.helium.strawberry.components.ar;


import eu.kudan.kudan.ARAPIKey;

public class KudanSetup {

    /**
     * Authenticates Kudan's API key and returns success flag
     * @return false if failed, true otherwise
     */
    public static boolean setupKudan() {
        ARAPIKey key = ARAPIKey.getInstance();
        key.setAPIKey("Qwi7gzfb8JM20A/Z6b76VA5xIdTQXupW3hEOPpIvJpmkAzcogi1pclIB9uRPywzrr+e8lqFwfc4f6AoOr0njGyb6zlf7MIwEa9XPOTMM7x/Exj9AwLzOAxgffIwSIlLFw/8sE7+kMWqXpQYs96tP5alhsONu/uJrB+N5Y15WljardFLcSOGRXYUWXN/bCeB2+du4vqu3fatqHU9CYd6Q0u1HBLdBs9pDVzjHViDXMjTsTHQG9CPqJkCTk/NoU2/np2UiciqJLRtkz9qK5j0vkNUsIhXa03JLNFIjCgKscaqz8uNWn9pbfLCYp6c/JLnejcJJMsvf1Kj+DEEOV7MvC833JUQb+9zBozF9P8ktu06d4/rBhZEGW+Lo51FjQAHKrUlv666oh28qb3fP+U+0h5G+4tAITjYAT2bTfrNod0VGRXQH8NpggSaF1dJOWC2UVH3g25aHvf/6vNj0LUfkNYTXWbVUtOyWdQz0vpvbvUfdPdMZ65D7nT2qYbyC1NPUWQboH1+r3p33Xw2BYdcWVVeVMOlQoLVn/AeSfj1TJnjhN74oiy2eT5iyRV3S5HWkorFjrO7TJ5sH9XMvbDcr22heMMY2tV4vtQ4LEz+BMUzmjev/jTyGo4C1ED2VUDnLrfeXDRrjL6IcMR8mk1lxl9p0EIQpQOFXqC2GbZ7Y4XQ=");
        return key.licenseKeyIsValid();
    }
}
